package db_access.dao._match

import db_access.dao.city.CityDao
import db_access.dao.unit.UnitDao
import db_access.table.MatchesTable
import models.Common.Fixture
import models._match.PlayedMatch
import models._match.result.{Draw, WinA, WinB}
import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend

class MatchDaoImpl(implicit inj: Injector) extends MatchDao with Injectable {
  private val ds = TableQuery[MatchesTable]

  private val cityDao = inject[CityDao]

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models._match.Match] =
    (for (m <- ds if m.id === id) yield m).firstOption match {
      case None => None
      case Some((unitId: Long, fixtureNum: Int, aTeamId: Long, aTeamGoals: Option[Int], bTeamId: Long, bTeamGoals: Option[Int], date: Option[DateTime])) =>
        val aCity = cityDao.fromId(aTeamId).get
        val bCity = cityDao.fromId(bTeamId).get

        if (aTeamGoals.isEmpty || bTeamGoals.isEmpty) {
          Some(models._match.Match(aCity, bCity, Some(id)))
        } else {
          val goalsDiff = aTeamGoals.get - bTeamGoals.get
          val result =
            if (goalsDiff > 0) WinA(aTeamGoals.get, bTeamGoals.get)
            else if (goalsDiff == 0) Draw(aTeamGoals.get)
            else WinB(aTeamGoals.get, bTeamGoals.get)

          Some(models._match.PlayedMatch(aCity, bCity, result, date, Some(id)))
        }
    }

  override def saveOrUpdate(m: models._match.Match, fixtureNum: Int, unitId: Long)(implicit rs: JdbcBackend#Session): Long =
    if (m.id.nonEmpty && fromId(m.id.get).nonEmpty) update(m, fixtureNum, unitId)
    else save(m, fixtureNum, unitId)

  private def save(m: models._match.Match, fixtureNum: Int, unitId: Long)(implicit rs: JdbcBackend#Session): Long =
    (ds returning ds.map(_.id)) += mapMatchToMatchesTable(m, fixtureNum, unitId)

  private def update(m: models._match.Match, fixtureNum: Int, unitId: Long)(implicit rs: JdbcBackend#Session): Long =
    ds.filter(_.id === m.id.get).update(mapMatchToMatchesTable(m, fixtureNum, unitId))

  private def mapMatchToMatchesTable(m: models._match.Match, fixtureNum: Int, unitId: Long) =
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

  override def getFixturesWithinUnit(id: Long)(implicit rs: JdbcBackend#Session): List[Fixture] =
    groupMatchesByFixture(ds.filter(_.unitId === id).map(m => (m.id, m.fixtureNum)).list.map(a => (fromId(a._1).get, a._2)))

  private def groupMatchesByFixture(matchFixtureList: List[(models._match.Match, Int)]): List[Fixture] =
    matchFixtureList.groupBy(_._2).toList.sortBy(_._1).map(_._2.map(_._1))
}
