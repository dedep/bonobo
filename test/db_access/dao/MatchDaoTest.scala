package db_access.dao

import db_access.dao._match.{MatchDaoImpl, MatchDao}
import db_access.dao.round.{RoundDaoImpl, RoundDao}
import db_access.dao.unit.{UnitDao, UnitDaoImpl}
import models._match.PlayedMatch
import models._match.result.{Draw, WinB}
import models.round.pair.Pair
import models.territory.{Territory, City}
import modules.MockModule
import org.joda.time.DateTime
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.test.WithApplication
import scaldi.{Module, Injectable}

import modules.DBMock._

class MatchDaoTest extends Specification with Injectable with Mockito {

  implicit val module = new Module { bind [MatchDao] to new MatchDaoImpl() } :: new MockModule

  lazy val matchDao = inject[MatchDao]

  "Match.fromId returns proper Match" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val m = matchDao.fromId(2)

      //then
      m should beSome[models._match.Match]
      m.get should beAnInstanceOf[PlayedMatch]
      m.get.id.get shouldEqual 2

      val pm = m.get.asInstanceOf[PlayedMatch]

      pm.aTeam.name shouldEqual "Ustrzyki Dolne"
      pm.bTeam.name shouldEqual "Ustrzyki Górne"

      pm.result.aGoals shouldEqual 5
      pm.result.bGoals shouldEqual 9
    }
  }

  "Match.fromId returns None when there is no such a match" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)

      //when
      val m = matchDao.fromId(1222)

      //then
      m should beEmpty
    }
  }

  "Unplayed match saving test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val m = new models._match.Match(city1, city2)

      //when
      val mId = matchDao.saveOrUpdate(m, 1, 1)

      //then
      val query = session.prepareStatement("SELECT id, unit_id, fixture, a_team_id, a_team_goals, b_team_id, b_team_goals FROM matches WHERE id = ?")
      query.setLong(1, mId)
      val result = query.executeQuery()
      result.next()

      result.getLong(1) shouldEqual mId
      result.getLong(2) shouldEqual 1
      result.getInt(3) shouldEqual 1
      result.getLong(4) shouldEqual 1
      result.getObject(5) should beNull
      result.getLong(6) shouldEqual 2
      result.getObject(7) should beNull
      result.next() should beFalse
    }
  }

  "Played match saving test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val m = new PlayedMatch(city1, city2, WinB(2, 4), Some(DateTime.now()))

      //when
      val mId = matchDao.saveOrUpdate(m, 1, 1)

      //then
      val query = session.prepareStatement("SELECT id, unit_id, fixture, a_team_id, a_team_goals, b_team_id, b_team_goals FROM matches WHERE id = ?")
      query.setLong(1, mId)
      val result = query.executeQuery()
      result.next()

      assert(result.getLong(1) === mId)
      assert(result.getLong(2) === 1)
      assert(result.getInt(3) === 1)
      assert(result.getLong(4) === 1)
      assert(result.getInt(5) === 2)
      assert(result.getLong(6) === 2)
      assert(result.getInt(7) === 4)
      assert(!result.next())
    }
  }

  "Match update test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val m = new PlayedMatch(city1, city2, Draw(2), Some(DateTime.now()))

      //when
      val mId = matchDao.saveOrUpdate(m, 0, 1)

      //then
      val query = session.prepareStatement("SELECT id, unit_id, fixture, a_team_id, a_team_goals, b_team_id, b_team_goals FROM matches WHERE id = ?")
      query.setLong(1, mId)
      val result = query.executeQuery()
      result.next()

      result.getLong(1) shouldEqual mId
      result.getLong(2) shouldEqual 1
      result.getInt(3) shouldEqual 0
      result.getLong(4) shouldEqual 1
      result.getInt(5) shouldEqual 2
      result.getLong(6) shouldEqual 2
      result.getInt(7) shouldEqual 2
      result.next() should beFalse
    }
  }

  "Get fixtures test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val fixtures = matchDao.getFixturesWithinUnit(1)

      //then
      fixtures should have size 2
      fixtures(0).map(_.id.get) should containAllOf(1 :: 2 :: Nil)
      fixtures(1).map(_.id.get) should containAllOf(3 :: 4 :: Nil)
    }
  }
}