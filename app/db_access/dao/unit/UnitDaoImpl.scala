package db_access.dao.unit

import com.typesafe.scalalogging.slf4j.Logger
import db_access.dao._match.MatchDao
import db_access.dao.city.CityDao
import db_access.table.{UnitsCitiesTable, UnitsTable}
import models.Common._
import models.round.RoundUnit
import models.round.result.TeamResult
import models.team.Team
import models.territory.City
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend
import utils.FunLogger._

class UnitDaoImpl(implicit inj: Injector) extends UnitDao with Injectable {
  private val ds = TableQuery[UnitsTable]
  private val citiesDs = TableQuery[UnitsCitiesTable]

  private val matchDao = inject[MatchDao]
  private val cityDao = inject[CityDao]

  private implicit val log = Logger(LoggerFactory.getLogger(this.getClass))

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[RoundUnit] =
    (for (m <- ds if m.id === id) yield m).firstOption match {
      case None => None
      case Some((roundId: Long, clazz: String, name: String)) =>
        instantiateUnitFromTableRow(name, id, clazz, roundId)
    }

  private def instantiateUnitFromTableRow(name: String, id: Long, clazz: String, roundId: Long)
                                         (implicit rs: JdbcBackend#Session): Option[RoundUnit] = {
    lazy val citiesAndResults: (List[City], List[TeamResult], List[Option[Boolean]]) = getCitiesAndResults(id)
    val fixtures = () => matchDao.getFixturesWithinUnit(id)
    val cities = () => citiesAndResults._1
    val results = () => citiesAndResults._2
    val promotions = () => getPromotedTeams(citiesAndResults._3, citiesAndResults._1)
    val eliminations = () => getEliminatedTeams(citiesAndResults._3, citiesAndResults._1)

    Some(Class.forName(clazz)
      .getConstructor(classOf[String], classOf[() => List[Team]], classOf[() => List[Fixture]], classOf[() => List[TeamResult]],
        classOf[Option[Long]], classOf[Boolean], classOf[() => List[Team]], classOf[() => List[Team]])
      .newInstance(name, cities, fixtures, results, Some(id), Boolean.box(false), promotions, eliminations)
      .asInstanceOf[RoundUnit])
  }

  private def getCitiesAndResults(id: Long)(implicit rs: JdbcBackend#Session): (List[City], List[TeamResult], List[Option[Boolean]]) = {
    log.info("Before Query-ing for cities and results for unit: " + id)
    val data = citiesDs.filter(_.unitId === id).list
    val cities = cityDao.fromId(data.map(_._1))
    val results = data.map(row => TeamResult(cities.find(_.id == row._1).get, row._3, row._4, row._5, row._6, row._7, row._8))
    val promoted = cities.map(c => data.find(_._1 == c.id).get._9)
    log.info("After Query-ing for cities and results for unit: " + id)

    (cities, results, promoted)
  }

  private def getPromotedTeams(dbPromotedTeams: List[Option[Boolean]], teams: List[Team]): List[Team] =
    getTeamsWithPromotionStatus(dbPromotedTeams, teams, true)

  private def getEliminatedTeams(dbPromotedTeams: List[Option[Boolean]], teams: List[Team]) =
    getTeamsWithPromotionStatus(dbPromotedTeams, teams, false)

  private def getTeamsWithPromotionStatus(dbPromotedTeams: List[Option[Boolean]], teams: List[Team], status: Boolean): List[Team] =
    dbPromotedTeams.zip(teams).filter(x => x._1.isDefined && x._1.get == status).map(_._2)

  override def saveOrUpdate(u: RoundUnit, parentRoundId: Long, fixturesToUpdate: Set[Int])(implicit rs: JdbcBackend#Session): Long = {
    if (u.id.nonEmpty && fromId(u.id.get).nonEmpty) update(u, parentRoundId, fixturesToUpdate)
    else save(u, parentRoundId)
  }

  override def getAllWithinRound(roundId: Long)(implicit rs: JdbcBackend#Session): List[RoundUnit] =
    ds.filter(_.roundId === roundId).sortBy(_.name).map(r => (r.id, r.roundId, r.clazz, r.name))
      .log(x => "Before Query-ing for units in round " + roundId).info()
      .list.map{ case (id: Long, roundId: Long, clazz: String, name: String) =>
        instantiateUnitFromTableRow(name, id, clazz, roundId).get
      }
      .log(x => "After Query-ing for units in round " + roundId).info()

  override def getPromotedTeamsWithinRound(roundId: Long)(implicit rs: JdbcBackend#Session): List[Team] = {
    val ids = (for {
      unit <- ds
      cityUnit <- citiesDs
      if unit.id === cityUnit.unitId
      if unit.roundId === roundId
      if cityUnit.promoted === true
    } yield cityUnit.cityId).list

    cityDao.fromId(ids)
  }

  private def save(u: RoundUnit, parentRoundId: Long)(implicit rs: JdbcBackend#Session): Long = {
    val newIndex = (ds returning ds.map(_.id)) += (parentRoundId, u.getClass.getName, u.name)

    citiesDs ++= u.teams.map(team => {
      val teamResult = u.results.find(_.team == team).getOrElse(throw new IllegalStateException("Cannot find CityResult"))
      (team.id,
        newIndex,
        teamResult.points,
        teamResult.goalsScored,
        teamResult.goalsConceded,
        teamResult.wins,
        teamResult.draws,
        teamResult.loses,
        isTeamPromoted(u, team))
    })

    u.fixtures.zipWithIndex.foreach(p => p._1.foreach(matchDao.saveOrUpdate(_, p._2, newIndex)))

    newIndex
  }

  private def update(u: RoundUnit, parentRoundId: Long, fixturesToUpdate: Set[Int])(implicit rs: JdbcBackend#Session): Long = {
    u.teams.foreach(team => {
      val teamResult =
        u.results.find(_.team.id == team.id).getOrElse(throw new IllegalStateException())

      citiesDs.filter(c => c.cityId === team.id && c.unitId === u.id.get).update(team.id, u.id.get,
        teamResult.points,
        teamResult.goalsScored,
        teamResult.goalsConceded,
        teamResult.wins,
        teamResult.draws,
        teamResult.loses,
        isTeamPromoted(u, team))
    })

    if (u.fixtures.nonEmpty && fixturesToUpdate.nonEmpty && fixturesToUpdate.forall(_ >= 0))
      fixturesToUpdate.foreach(i => u.fixtures(i).foreach(m => matchDao.saveOrUpdate(m, i, u.id.get)))

    u.id.get
  }

  private def isTeamPromoted(unit: RoundUnit, team: Team): Option[Boolean] =
    unit.promotedTeams.contains(team) match {
      case true => Some(true)
      case false => unit.eliminatedTeams.contains(team) match {
        case true => Some(false)
        case false => None
      }
    }
}
