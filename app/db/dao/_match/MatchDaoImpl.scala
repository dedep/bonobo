package db.dao._match

import com.typesafe.scalalogging.slf4j.Logger
import db.dao.city.CityDao
import db.dao.tournament.TournamentRulesDao
import db.table._
import models.Common.Fixture
import models._match.PlayedMatch
import models._match.result.{Draw, WinA, WinB}
import models.reverse.{RoundUnitInfo, TournamentInfo, RoundInfo}
import models.territory.City
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}
import utils.FunLogger._

import scala.slick.jdbc.JdbcBackend

class MatchDaoImpl(implicit inj: Injector) extends MatchDao with Injectable {
  private val ds = TableQuery[MatchesTable]

  private val cityDao = inject[CityDao]
  private val rulesDao = inject[TournamentRulesDao]

  private implicit val log = Logger(LoggerFactory.getLogger(this.getClass))

  val selectQuery =
    for {
      m <- ds
      aCity <- cityDao.selectQuery
      bCity <- cityDao.selectQuery
      if m.aTeamId === aCity._1.id
      if m.bTeamId === bCity._1.id
    } yield (m, aCity, bCity)

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models._match.Match] =
    fromFilter(_.id === id).headOption

  override def fromId(ids: Seq[Long])(implicit rs: JdbcBackend#Session): List[models._match.Match] =
    fromFilter(_.id inSet ids)

  private def fromFilter(filter: MatchesTable => Column[Boolean])(implicit rs: JdbcBackend#Session): List[models._match.Match] =
    selectQuery
      .filter(x => filter(x._1))
      .list
      .map { row =>
        val aTeam = cityDao.fromRow(row._2)
        val bTeam = cityDao.fromRow(row._3)

        instantiateMatchFromTableRow(row._1.id, row._1.unitId, row._1.fixtureNum, aTeam, row._1.aTeamGoals, bTeam,
          row._1.bTeamGoals, row._1.playDate)
      }

  private def instantiateMatchFromTableRow(id: Long, unitId: Long, fixtureNum: Int, aCity: City, aTeamGoals: Option[Int],
    bCity: City, bTeamGoals: Option[Int], date: Option[DateTime])(implicit rs: JdbcBackend#Session): models._match.Match = {

    val unitInfo = getReverseUnitInfo(unitId)

    if (aTeamGoals.isEmpty || bTeamGoals.isEmpty) {
      new models._match.Match(aCity, bCity, Some(id))(unitInfo)
    } else {
      val goalsDiff = aTeamGoals.get - bTeamGoals.get
      val result =
        if (goalsDiff > 0) WinA(aTeamGoals.get, bTeamGoals.get)
        else if (goalsDiff == 0) Draw(aTeamGoals.get)
        else WinB(aTeamGoals.get, bTeamGoals.get)

      models._match.PlayedMatch(aCity, bCity, result, date, Some(id))(unitInfo)
    }
  }

  override def saveOrUpdate(m: models._match.Match, fixtureNum: Int, unitId: Long)(implicit rs: JdbcBackend#Session): Long =
    if (m.id.nonEmpty && fromId(m.id.get).nonEmpty) update(m, fixtureNum, unitId)
    else save(m, fixtureNum, unitId)


  private def save(m: models._match.Match, fixtureNum: Int, unitId: Long)(implicit rs: JdbcBackend#Session): Long =
    ds.map(_.autoInc) += NewMatchDBRow.tupled(mapNewMatchToMatchesTable(m, fixtureNum, unitId))

  private def update(m: models._match.Match, fixtureNum: Int, unitId: Long)(implicit rs: JdbcBackend#Session): Long = {
    ds.filter(_.id === m.id.get).update(
      MatchDBRow.tupled(mapMatchToMatchesTable(m, fixtureNum, unitId))
    )
  }

  private def mapMatchToMatchesTable(m: models._match.Match, fixtureNum: Int, unitId: Long) =
    mapNewMatchToMatchesTable(m, fixtureNum, unitId) match {
      case (unitId: Long, fixtureNum: Int, aTeamId: Long, aTeamGoals: Option[Int], bTeamId: Long,
      bTeamGoals: Option[Int], playDate: Option[DateTime]) =>
        (m.id.get, unitId, fixtureNum, aTeamId, aTeamGoals, bTeamId, bTeamGoals, playDate)
    }

  private def mapNewMatchToMatchesTable(m: models._match.Match, fixtureNum: Int, unitId: Long) =
    (
      unitId,
      fixtureNum,
      m.aTeam.id,
      m match {
        case playedMatch: PlayedMatch => Some(playedMatch.result.aGoals)
        case _ => None
      },
      m.bTeam.id,
      m match {
        case playedMatch: PlayedMatch => Some(playedMatch.result.bGoals)
        case _ => None
      },
      m.playDate
    )

  override def getFixturesWithinUnit(unitId: Long)(implicit rs: JdbcBackend#Session): List[Fixture] = {
    val matchesFixturesIds: List[(Long, Int)] =
      ds.filter(_.unitId === unitId).map(m => (m.id, m.fixtureNum))
      .log(x => "Querying for matches in unit " + unitId).info()
      .list

    val zippedMatchesAndFixtures =
      fromId(matchesFixturesIds.map(_._1)).map(r => (r, matchesFixturesIds.find(_._1 == r.id.get).get._2))

    groupMatchesByFixture(zippedMatchesAndFixtures)
  }

  private def groupMatchesByFixture(matchFixtureList: List[(models._match.Match, Int)]): List[Fixture] =
    matchFixtureList.groupBy(_._2).toList.sortBy(_._1).map(_._2.map(_._1))

  private def getReverseUnitInfo(unitId: Long)(implicit rs: JdbcBackend#Session): RoundUnitInfo =
    (for {
      tournament <- TableQuery[TournamentsTable]
      round <- TableQuery[RoundsTable]
      unit <- TableQuery[UnitsTable]
      if unit.id === unitId
      if round.id === unit.roundId
      if tournament.id === round.tournamentId
    } yield (tournament.name, tournament.id, round.name, round.id, unit.name, unit.id))
      .firstOption
      .map(r => {
        val tournamentInfo = new TournamentInfo(r._1, Some(r._2), rulesDao.fromTournamentId(r._2)
          .getOrElse(throw new IllegalStateException("Round calls to non-existent tournament " + r._2))
        )
        val roundInfo = new RoundInfo(tournamentInfo)(r._3, Some(r._4))
        new RoundUnitInfo(roundInfo)(r._5, Some(r._6))
    })
      .getOrElse(throw new IllegalStateException("Inconsistent DB state while looking for reverse unit info " + unitId))
}
