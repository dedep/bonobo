package db.dao

import com.typesafe.scalalogging.slf4j.Logger
import db.table.{RoundsTable, TournamentsTable, UnitsCitiesTable, UnitsTable}
import models.reverse.{RoundInfo, TournamentInfo}
import models.territory.City
import models.unit.{Group, Pair, RoundUnit, UnitTeamResult}
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}
import utils.FunLogger._

import scala.slick.jdbc.JdbcBackend

class UnitDaoImpl(implicit inj: Injector) extends UnitDao with Injectable {
  private val ds = TableQuery[UnitsTable]
  private val citiesDs = TableQuery[UnitsCitiesTable]

  private val matchDao = inject[MatchDao]
  private val cityDao = inject[CityDao]
  private val rulesDao = inject[TournamentRulesDao]

  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[RoundUnit] =
    (for (m <- ds if m.id === id) yield m).firstOption match {
      case None => None
      case Some((roundId: Long, clazz: String, name: String)) =>
        instantiateUnitFromTableRow(name, id, clazz, roundId, None)
    }

  private def instantiateUnitFromTableRow(name: String, id: Long, clazz: String, roundId: Long, info: Option[RoundInfo])
                                         (implicit rs: JdbcBackend#Session): Option[RoundUnit] = {
    lazy val citiesAndResults: (List[City], List[UnitTeamResult], List[Option[Boolean]]) = getCitiesAndResults(id)
    lazy val fixtures = matchDao.getFixturesWithinUnit(id)
    lazy val cities = citiesAndResults._1
    lazy val results = citiesAndResults._2
    lazy val promotions = getPromotedTeams(citiesAndResults._3, citiesAndResults._1)
    lazy val eliminations = getEliminatedTeams(citiesAndResults._3, citiesAndResults._1)
    val roundInfo = info.getOrElse(getReverseRoundInfo(roundId))

    if (classOf[Pair].getName.equals(clazz)) {
      Some(new Pair(name, cities, fixtures, results, Some(id), false, promotions, eliminations)(roundInfo))
    } else if (classOf[Group].getName.equals(clazz)) {
      Some(new Group(name, cities, fixtures, results, Some(id), false, promotions, eliminations)(roundInfo))
    } else {
      throw new IllegalStateException("Unknown unit class: " + clazz)
    }
  }

  private def getCitiesAndResults(id: Long)(implicit rs: JdbcBackend#Session): (List[City], List[UnitTeamResult], List[Option[Boolean]]) = {
    log.info("Before Query-ing for cities and results for unit: " + id)
    val data = citiesDs.filter(_.unitId === id).list
    val cities = cityDao.fromId(data.map(_._1))
    val results = data.map(row => UnitTeamResult(cities.find(_.id == row._1).get, row._3, row._4, row._5, row._6, row._7, row._8))
    val promoted = cities.map(c => data.find(_._1 == c.id).get._9)
    log.info("After Query-ing for cities and results for unit: " + id)

    (cities, results, promoted)
  }

  private def getPromotedTeams(dbPromotedTeams: List[Option[Boolean]], teams: List[City]): List[City] =
    getTeamsWithPromotionStatus(dbPromotedTeams, teams, true)

  private def getEliminatedTeams(dbPromotedTeams: List[Option[Boolean]], teams: List[City]) =
    getTeamsWithPromotionStatus(dbPromotedTeams, teams, false)

  private def getTeamsWithPromotionStatus(dbPromotedTeams: List[Option[Boolean]], teams: List[City], status: Boolean): List[City] =
    dbPromotedTeams.zip(teams).filter(x => x._1.isDefined && x._1.get == status).map(_._2)

  override def saveOrUpdate(u: RoundUnit, parentRoundId: Long, fixturesToUpdate: Set[Int])(implicit rs: JdbcBackend#Session): Long = {
    if (u.id.nonEmpty && fromId(u.id.get).nonEmpty) update(u, parentRoundId, fixturesToUpdate)
    else save(u, parentRoundId)
  }

  override def getAllWithinRound(roundId: Long)(implicit rs: JdbcBackend#Session): List[RoundUnit] = {
    val roundInfo = getReverseRoundInfo(roundId)

    ds.filter(_.roundId === roundId).sortBy(_.name).map(r => (r.id, r.roundId, r.clazz, r.name))
      .log(x => "Before Query-ing for units in round " + roundId).info()
      .list.map{ case (id: Long, roundId: Long, clazz: String, name: String) =>
      instantiateUnitFromTableRow(name, id, clazz, roundId, Some(roundInfo)).get
    }
      .log(x => "After Query-ing for units in round " + roundId).info()
  }

  override def getPromotedTeamsWithinRound(roundId: Long)(implicit rs: JdbcBackend#Session): List[City] = {
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
      (team.id.get,
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

      citiesDs.filter(c => c.cityId === team.id && c.unitId === u.id.get).update(team.id.get, u.id.get,
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

  private def isTeamPromoted(unit: RoundUnit, team: City): Option[Boolean] =
    unit.promotedTeams.contains(team) match {
      case true => Some(true)
      case false => unit.eliminatedTeams.contains(team) match {
        case true => Some(false)
        case false => None
      }
    }

  private def getReverseRoundInfo(roundId: Long)(implicit rs: JdbcBackend#Session): RoundInfo =
    (for {
      tournament <- TableQuery[TournamentsTable]
      round <- TableQuery[RoundsTable]
      if round.id === roundId
      if tournament.id === round.tournamentId
    } yield (tournament.name, tournament.id, round.name, round.id))
      .log(x => "Before executing query for reverse round " + roundId + " info").info()
      .firstOption
      .map(r => {
        val tournamentInfo = new TournamentInfo(r._1, Some(r._2), rulesDao.fromTournamentId(r._2)
          .getOrElse(throw new IllegalStateException("Round calls to non-existent tournament " + r._2))
        )
        new RoundInfo(tournamentInfo)(r._3, Some(r._4))
      })
      .getOrElse(throw new IllegalStateException("Inconsistent DB state while looking for reverse round info " + roundId))
      .log(x => "After executing query for reverse round " + roundId + " info").info()
}
