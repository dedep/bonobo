package db.dao

import com.typesafe.scalalogging.slf4j.Logger
import db.table.{RoundsCitiesTable, RoundsTable, TournamentsTable}
import models.Common.Pot
import models.reverse.TournamentInfo
import models.round.{GroupRound, PlayoffRound, Round, RoundStatus}
import models.territory.City
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}
import utils.FunLogger._

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

class RoundDaoImpl(implicit inj: Injector) extends RoundDao with Injectable {
  private val ds = TableQuery[RoundsTable]
  private val citiesDs = TableQuery[RoundsCitiesTable]

  private val unitDao = inject[UnitDao]
  private val cityDao = inject[CityDao]
  private val rulesDao = inject[TournamentRulesDao]

  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models.round.Round] =
    (for (round <- ds if round.id === id) yield round).firstOption match {
      case None => None
      case Some((name: String, clazz: String, step: Int, isPreliminary: Boolean, tournamentId: Long, status: String)) =>
        try {
          instantiateRoundFromTableRow(id, name, clazz, step, isPreliminary, tournamentId, status)
        } catch {
          case cnfe: ClassNotFoundException => log.error("Cannot find saved in database class", cnfe) ; None
          case cce: ClassCastException => log.error("Cannot cast saved in database class into Round", cce) ; None
        }
    }

  private def instantiateRoundFromTableRow(id: Long, name: String, clazz: String, step: Int, isPreliminary: Boolean,
                                           tournamentId: Long, status: String)(implicit rs: JdbcBackend#Session): Option[Round] = {

    lazy val group = getRoundCitiesAndPots(id)
    lazy val cities = getCities(group)
    lazy val pots = groupCitiesByPots(group)
    lazy val units = unitDao.getAllWithinRound(id)
    val tournamentInfo = getReverseTournamentInfo(tournamentId)

    if (isPreliminary) {
      if (classOf[PlayoffRound].getName.equals(clazz)) {
        Some(new PlayoffRound(name, cities, pots, units, step, true, Some(id), RoundStatus.withName(status))(tournamentInfo))
      } else {
        throw new IllegalStateException("Unknown preliminary round class: " + clazz)
      }
    } else {
      if (classOf[PlayoffRound].getName.equals(clazz)) {
        Some(new PlayoffRound(name, cities, pots, units, step, false, Some(id), RoundStatus.withName(status))(tournamentInfo))
      } else if (classOf[GroupRound].getName.equals(clazz)) {
        Some(new GroupRound(name, cities, pots, units, step, Some(id), RoundStatus.withName(status))(tournamentInfo))
      } else {
        throw new IllegalStateException("Unknown round class: " + clazz)
      }
    }
  }

  override def saveOrUpdate(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Long =
    if (r.id.nonEmpty) update(r, parentTournamentId)
    else save(r, parentTournamentId)

  private def save(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Before saving new round " + r.name)

    val newIndex = (ds returning ds.map(_.id)) += (r.name, r.getClass.getName, r.stepIndex,
      r.isPreliminary, parentTournamentId, r.status.toString)

    citiesDs ++= r.teams.map(city => (newIndex, city.id.get,
      r.pots.zipWithIndex.filter(p => p._1.contains(city)).map(_._2).headOption))

    log.info("After saving new round " + r.name)
    newIndex
  }

  private def update(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Before updating round " + r.name)

    ds.filter(_.id === r.id.get).map(x => (x.step, x.status)).update(r.stepIndex, r.status.toString)

    log.info("Before unit saveOrUpdateCall round " + r.name)
    if (r.units.nonEmpty) r.units.foreach(unitDao.saveOrUpdate(_, r.id.get, Set(r.stepIndex - 1)))
    log.info("After unit saveOrUpdateCall round " + r.name)

    r.teams.foreach { t =>
      citiesDs
        .filter(c => c.roundId === r.id && c.cityId === t.id).map(_.pot)
        .update(r.pots.zipWithIndex.filter(p => p._1.contains(t)).map(_._2).headOption)
    }

    log.info("After updating round " + r.name)
    r.id.get
  }

  private def getRoundCitiesAndPots(roundId: Long)(implicit rs: JdbcBackend#Session): List[(City, Option[Int])] = {
    log.info("Before Querying for cities and pots for round: " + roundId)
    (for (roundCity <- citiesDs if roundCity.roundId === roundId) yield roundCity).map(r => (r.cityId, r.pot)).list match {
      case (citiesPots: List[(Long, Option[Int])]) =>
        cityDao.fromId(citiesPots.map(_._1)).zip(citiesPots.map(_._2))
          .log(x => "Querying for cities and pots for round: " + roundId).info()
    }
  }

  private def groupCitiesByPots(citiesPots: => List[(City, Option[Int])]): List[Pot] =
    citiesPots.filter(_._2.nonEmpty).groupBy(_._2.get).toList.sortBy(_._1).map(_._2.map(_._1))

  private def getCities(citiesPots: => List[(City, Option[Int])]): List[City] = citiesPots.map(_._1)

  private def getReverseTournamentInfo(tournamentId: Long)(implicit rs: JdbcBackend#Session): TournamentInfo =
    TableQuery[TournamentsTable]
      .filter(_.id === tournamentId)
      .map(_.name)
      .firstOption
      .map(n => new TournamentInfo(n, Some(tournamentId), rulesDao.fromTournamentId(tournamentId)
        .getOrElse(throw new IllegalStateException("Cannot find rules for tournament " + tournamentId)))
      )
      .getOrElse(throw new IllegalStateException("Round calls to non-existent tournament " + tournamentId))

  override def getTournamentRounds(id: Long)(implicit rs: JdbcBackend#Session): List[Round] =
    ds.filter(_.tournamentId === id).sortBy(_.id desc).map(_.id)
      .log(x => "Query-ing for rounds for tournament = " + id).info()
      .list
      .map(fromId(_)
      .getOrElse(throw new IllegalStateException()))
}