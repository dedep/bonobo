package db_access.dao.round

import com.typesafe.scalalogging.slf4j.Logger
import db_access.dao.city.CityDao
import db_access.dao.tournament.TournamentDao
import db_access.dao.unit.UnitDao
import db_access.table.{RoundsCitiesTable, RoundsTable}
import models.Common.Pot
import models.round.pair.PlayoffRound
import models.round.{Round, RoundUnit}
import models.team.Team
import models.territory.City
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

class RoundDaoImpl(implicit inj: Injector) extends RoundDao with Injectable {
  private val ds = TableQuery[RoundsTable]
  private val citiesDs = TableQuery[RoundsCitiesTable]

  private val unitDao = inject[UnitDao]
  private val cityDao = inject[CityDao]

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models.round.Round] =
    (for (round <- ds if round.id === id) yield round).firstOption match {
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

    val units = unitDao.getAllWithinRound(id)

    if (isPreliminary) {
      Some(Class.forName(clazz)
        .getConstructor(classOf[String], classOf[() => List[Team]], classOf[() => List[Pot]], classOf[() => List[RoundUnit]],
          classOf[Int], classOf[Boolean], classOf[Option[Long]])
        .newInstance(name, () => cities, () => pots, () => units, Int.box(step), Boolean.box(true), Some(id))
        .asInstanceOf[models.round.Round])
    } else {
      Some(Class.forName(clazz)
        .getConstructor(classOf[String], classOf[() => List[Team]], classOf[() => List[Pot]], classOf[() => List[RoundUnit]],
          classOf[Int], classOf[Option[Long]])
        .newInstance(name, () => cities, () => pots, () => units, Int.box(step), Some(id))
        .asInstanceOf[models.round.Round])
    }
  }

  override def saveOrUpdate(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Before rounds updating in following tournament: " + parentTournamentId)

    val ret = if (r.id.nonEmpty && fromId(r.id.get).nonEmpty) update(r, parentTournamentId)
    else save(r, parentTournamentId)

    citiesDs.filter(_.roundId === ret).delete
    citiesDs ++= r.teams.map(city => (ret, city.id,
      r.pots.zipWithIndex.filter(p => p._1.contains(city)).map(_._2).headOption))

    log.info("After rounds updating in following tournament: " + parentTournamentId)

    ret
  }

  private def save(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Before saving new round " + r.name)

    val newIndex = (ds returning ds.map(_.id)) += (r.name, r.getClass.getName, r.stepIndex,
      r.isInstanceOf[PlayoffRound] && r.asInstanceOf[PlayoffRound].preliminary, parentTournamentId)

    if (r.units.nonEmpty) r.units.foreach(unitDao.saveOrUpdate(_, newIndex))

    log.info("After saving new round " + r.name)
    newIndex
  }

  private def update(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Before updating round " + r.name)

    ds.filter(_.id === r.id.get).update((r.name, r.getClass.getName, r.stepIndex,
      r.isInstanceOf[PlayoffRound] && r.asInstanceOf[PlayoffRound].preliminary, parentTournamentId))

    log.info("Before unit saveOrUpdateCall round " + r.name)
    if (r.units.nonEmpty) r.units.foreach(unitDao.saveOrUpdate(_, r.id.get))
    log.info("After unit saveOrUpdateCall round " + r.name)

    log.info("After updating round " + r.name)
    r.id.get
  }

  private def getRoundCitiesAndPots(roundId: Long)(implicit rs: JdbcBackend#Session): List[(City, Option[Int])] =
    (for (roundCity <- citiesDs if roundCity.roundId === roundId) yield roundCity)
      .list.map(rc => (cityDao.fromId(rc._2).get, rc._3))

  private def groupCitiesByPots(citiesPots: List[(City, Option[Int])]): List[Pot] =
    citiesPots.filter(_._2.nonEmpty).groupBy(_._2.get).toList.sortBy(_._1).map(_._2.map(_._1))

  override def getTournamentRounds(id: Long)(implicit rs: JdbcBackend#Session): List[Round] =
    ds.filter(_.tournamentId === id).sortBy(_.order desc).map(_.id).list.map(fromId(_)
      .getOrElse(throw new IllegalStateException()))
}