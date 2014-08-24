package models.db_model

import com.typesafe.scalalogging.slf4j.Logger
import models.core.Common.Pot
import models.core.round.pair.PlayoffRound
import models.core.round.{Round, RoundUnit}
import models.core.team.Team
import models.core.tournament.Tournament
import models.table.{RoundsCitiesTable, RoundsTable}
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._

import play.api.db.slick._
import play.api.mvc.AnyContent

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

object Round {
  val ds = TableQuery[RoundsTable]
  val citiesDs = TableQuery[RoundsCitiesTable]

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models.core.round.Round] =
    (for (round <- Round.ds if round.id === id) yield round).firstOption match {
      case None => None
      case Some((clazz: String, step: Int, isPreliminary: Boolean, tournamentId: Long)) =>
        try { // todo: wydzielić metodę do testów!!
          val citiesPots = getRoundCitiesAndPots(id)
          val cities = citiesPots.map(_._1)
          val pots = groupCitiesByPots(citiesPots)

          val units = Unit.ds.filter(_.roundId === id).map(_.id).list.map(Unit.fromId(_).get)

          if (isPreliminary) {
            Some(Class.forName(clazz)
              .getConstructor(classOf[() => List[Team]], classOf[() => List[Pot]], classOf[() => List[RoundUnit]], classOf[Int], classOf[Boolean], classOf[Option[Long]])
              .newInstance(() => cities, () => pots, () => units, Int.box(step), Boolean.box(true), Some(id))
              .asInstanceOf[models.core.round.Round])
          } else {
            Some(Class.forName(clazz)
              .getConstructor(classOf[() => List[Team]], classOf[() => List[Pot]], classOf[() => List[RoundUnit]], classOf[Int], classOf[Option[Long]])
              .newInstance(() => cities, () => pots, () => units, Int.box(step), Some(id))
              .asInstanceOf[models.core.round.Round])
          }
        } catch {
          case cnfe: ClassNotFoundException => log.error("Cannot find saved in database class", cnfe) ; None
          case cce: ClassCastException => log.error("Cannot cast saved in database class into Round", cce) ; None
        }
    }

  //todo: trykacz
  def saveOrUpdate(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Option[Long] =
    if (r.id.isEmpty) save(r, parentTournamentId)
    else update(r, parentTournamentId)

  private def save(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Option[Long] = {
    val newIndex = (ds returning ds.map(_.id)) += (r.getClass.getName, r.stepIndex,
      r.isInstanceOf[PlayoffRound] && r.asInstanceOf[PlayoffRound].preliminary, parentTournamentId)

    citiesDs ++= r.teams.map(city => (newIndex, city.id,
      r.pots.zipWithIndex.filter(p => p._1.contains(city)).map(_._2).headOption)) //todo: powtórzyłem tą linię

    if (r.units.nonEmpty) r.units.foreach(Unit.saveOrUpdate(_, newIndex))

    Some(newIndex)
  }

  private def update(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Option[Long] = {
    ds.filter(_.id === r.id.get).update((r.getClass.getName, r.stepIndex,
      r.isInstanceOf[PlayoffRound] && r.asInstanceOf[PlayoffRound].preliminary, parentTournamentId))

    r.teams.foreach(team => citiesDs.insertOrUpdate(r.id.get, team.id,
      r.pots.zipWithIndex.filter(p => p._1.contains(team)).map(_._2).headOption)) // todo: O TU -- LOOK ABOVE ---

    if (r.units.nonEmpty) r.units.foreach(Unit.saveOrUpdate(_, r.id.get))

    r.id
  }

  private def getRoundCitiesAndPots(roundId: Long)(implicit rs: JdbcBackend#Session): List[(City, Option[Int])] =
    (for (roundCity <- citiesDs if roundCity.roundId === roundId) yield roundCity)
      .list.map(rc => (City.fromId(rc._2).get, rc._3))

  //todo: test
  private def groupCitiesByPots(citiesPots: List[(City, Option[Int])]): List[Pot] =
    citiesPots.filter(_._2.nonEmpty).groupBy(_._2).toList.map(_._2.map(_._1))
}