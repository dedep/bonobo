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
      case Some((name: String, clazz: String, step: Int, isPreliminary: Boolean, tournamentId: Long)) =>
        try {
          instantiateRoundFromTableRow(id, name, clazz, step, isPreliminary, tournamentId)
        } catch {
          case cnfe: ClassNotFoundException => log.error("Cannot find saved in database class", cnfe) ; None
          case cce: ClassCastException => log.error("Cannot cast saved in database class into Round", cce) ; None
        }
    }

  private def instantiateRoundFromTableRow(id: Long, name: String, clazz: String, step: Int, isPreliminary: Boolean, tournamentId: Long)
                                          (implicit rs: JdbcBackend#Session): Option[Round] = {
    val citiesPots = getRoundCitiesAndPots(id)
    val cities = citiesPots.map(_._1)
    val pots = groupCitiesByPots(citiesPots)

    val units = Unit.ds.filter(_.roundId === id).sortBy(_.name).map(_.id).list.map(Unit.fromId(_).get)

    if (isPreliminary) {
      Some(Class.forName(clazz)
        .getConstructor(classOf[String], classOf[() => List[Team]], classOf[() => List[Pot]], classOf[() => List[RoundUnit]],
          classOf[Int], classOf[Boolean], classOf[Option[Long]])
        .newInstance(name, () => cities, () => pots, () => units, Int.box(step), Boolean.box(true), Some(id))
        .asInstanceOf[models.core.round.Round])
    } else {
      Some(Class.forName(clazz)
        .getConstructor(classOf[String], classOf[() => List[Team]], classOf[() => List[Pot]], classOf[() => List[RoundUnit]],
          classOf[Int], classOf[Option[Long]])
        .newInstance(name, () => cities, () => pots, () => units, Int.box(step), Some(id))
        .asInstanceOf[models.core.round.Round])
    }
  }

  def saveOrUpdate(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Before rounds updating in following tournament: " + parentTournamentId)

    if (Tournament.fromId(parentTournamentId).isEmpty)
      throw new IllegalStateException("Round cannot refer to non-existent tournament")

    val parentTournamentTeams = Tournament.fromId(parentTournamentId).get.teams
    if (!r.teams.forall(team => parentTournamentTeams.exists(t => t.id == team.id)))
      throw new IllegalStateException("Round cannot contain cities that are not from parent tournament")

    log.info("Before cities updating in following round: " + r.name)
//    r.teams.foreach(team => City.saveOrUpdate(team.asInstanceOf[City]))
    log.info("After cities updating in following round: " + r.name)

    val ret = if (r.id.nonEmpty && Round.fromId(r.id.get).nonEmpty) update(r, parentTournamentId)
    else save(r, parentTournamentId)

    log.info("After rounds updating in following tournament: " + parentTournamentId)

    ret
  }

  private def save(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Before saving new round " + r.name)

    val newIndex = (ds returning ds.map(_.id)) += (r.name, r.getClass.getName, r.stepIndex,
      r.isInstanceOf[PlayoffRound] && r.asInstanceOf[PlayoffRound].preliminary, parentTournamentId)

    citiesDs.filter(_.roundId === newIndex).delete
    citiesDs ++= r.teams.map(city => (newIndex, city.id,
      r.pots.zipWithIndex.filter(p => p._1.contains(city)).map(_._2).headOption))

    if (r.units.nonEmpty) r.units.foreach(Unit.saveOrUpdate(_, newIndex))

    log.info("After saving new round " + r.name)
    newIndex
  }

  private def update(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Before updating round " + r.name)

    ds.filter(_.id === r.id.get).update((r.name, r.getClass.getName, r.stepIndex,
      r.isInstanceOf[PlayoffRound] && r.asInstanceOf[PlayoffRound].preliminary, parentTournamentId))

    r.teams.foreach(team => citiesDs.filter(c => c.cityId === team.id && c.roundId === r.id.get).update(r.id.get, team.id,
      r.pots.zipWithIndex.filter(p => p._1.contains(team)).map(_._2).headOption))

    log.info("Before unit saveOrUpdateCall round " + r.name)
    if (r.units.nonEmpty) r.units.foreach(Unit.saveOrUpdate(_, r.id.get))
    log.info("After unit saveOrUpdateCall round " + r.name)

    log.info("After updating round " + r.name)
    r.id.get
  }

  private def getRoundCitiesAndPots(roundId: Long)(implicit rs: JdbcBackend#Session): List[(City, Option[Int])] =
    (for (roundCity <- citiesDs if roundCity.roundId === roundId) yield roundCity)
      .list.map(rc => (City.fromId(rc._2).get, rc._3))

  private def groupCitiesByPots(citiesPots: List[(City, Option[Int])]): List[Pot] =
    citiesPots.filter(_._2.nonEmpty).groupBy(_._2.get).toList.sortBy(_._1).map(_._2.map(_._1))
}