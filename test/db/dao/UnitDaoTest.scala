package db.dao

import db.dao.territory.TerritoryDao
import db.dao.unit.UnitDaoImpl
import models.reverse.{RoundInfo, TournamentInfo}
import models.round.PlayoffRound
import models.tournament.GameRules
import models.unit.{UnitTeamResult, Group, Pair, RoundUnit}
import modules.MockModule
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.test.WithApplication
import scaldi.{Module, Injectable}

import modules.DBMock._

class UnitDaoTest extends Specification with Injectable with Mockito {

  implicit val module = new Module { bind [UnitDao] to new UnitDaoImpl() } :: new MockModule

  lazy val territoryDao = inject[TerritoryDao]
  lazy val unitDao = inject[UnitDao]

  val tournamentInfo = new TournamentInfo("t", None, GameRules(0, 1, 3))
  val roundInfo = new RoundInfo(tournamentInfo)("rName", None)

  "Unit.fromId returns proper Unit" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val unit = unitDao.fromId(1)

      //then
      unit should beSome[RoundUnit]
      unit.get.id.get shouldEqual 1

      unit.get.fixturesCount shouldEqual 6
      unit.get.results.map(_.team.name) should containAllOf("Ustrzyki Dolne" :: "Rzeszów" :: "Przemyśl" :: Nil)
      unit.get.teams.map(_.name) should containAllOf("Ustrzyki Dolne" :: "Rzeszów" :: "Przemyśl" :: Nil)
    }
  }

  "Unit.fromId returns None when there is no such a unit" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)

      //when
      val unit = unitDao.fromId(1)

      //then
      unit should beEmpty
    }
  }

  "Unit saving test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val u = new Pair("Pair A", List(city2, city3), resultsCbn = List(UnitTeamResult(city2, 1, 2, 3),
        UnitTeamResult(city3, 4, 5, 6)))(roundInfo)

      //when
      val uId = unitDao.saveOrUpdate(u, 1, Set())

      //then
      val query = session.prepareStatement("SELECT u.id, u.round_id, u.class, uc.city_id, uc.points, uc.goals_scored, uc.goals_conceded" +
        " FROM units u LEFT JOIN units_cities uc ON u.id = uc.unit_id WHERE u.id = ?")
      query.setLong(1, uId)
      val result = query.executeQuery()
      result.next()

      result.getLong(1) shouldEqual uId
      result.getLong(2) shouldEqual 1
      result.getString(3) shouldEqual classOf[Pair].getName
      result.getInt(4) shouldEqual 2
      result.getInt(5) shouldEqual 1
      result.getInt(6) shouldEqual 2
      result.getInt(7) shouldEqual 3

      result.next()
      result.getInt(4) shouldEqual 3
      result.getInt(5) shouldEqual 4
      result.getInt(6) shouldEqual 5
      result.getInt(7) shouldEqual 6
      result.next() should beFalse
    }
  }

  "Unit updating test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val u = new Group("Group A", List(city1, city2, city3, city4), Nil,
        List(UnitTeamResult(city1, 6, 3, 5), UnitTeamResult(city2, 2, 3, 4), UnitTeamResult(city3, 4, 1, 2),
          UnitTeamResult(city4, 1, 1, 1)), Some(1))(roundInfo)

      //when
      unitDao.saveOrUpdate(u, 1, Set(0))

      //then
      val query = session.prepareStatement("SELECT u.id, u.round_id, u.class, uc.city_id, uc.points, uc.goals_scored, uc.goals_conceded" +
        " FROM units u LEFT JOIN units_cities uc ON u.id = uc.unit_id WHERE u.id = ?")
      query.setLong(1, 1)
      val result = query.executeQuery()
      result.next()

      result.getLong(1) shouldEqual 1
      result.getLong(2) shouldEqual 1
      result.getString(3) shouldEqual classOf[Group].getName
      result.getInt(4) shouldEqual 1
      result.getInt(5) shouldEqual 6
      result.getInt(6) shouldEqual 3
      result.getInt(7) shouldEqual 5

      result.next()
      result.getInt(4) shouldEqual 2
      result.getInt(5) shouldEqual 2
      result.getInt(6) shouldEqual 3
      result.getInt(7) shouldEqual 4

      result.next()
      result.getInt(4) shouldEqual 3
      result.getInt(5) shouldEqual 4
      result.getInt(6) shouldEqual 1
      result.getInt(7) shouldEqual 2

      result.next()
      result.getInt(4) shouldEqual 4
      result.getInt(5) shouldEqual 1
      result.getInt(6) shouldEqual 1
      result.getInt(7) shouldEqual 1
      result.next() should beFalse
    }
  }
}
