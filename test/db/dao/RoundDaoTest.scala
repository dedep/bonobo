package db.dao

import db.dao.UnitDao
import db.dao.round.RoundDaoImpl
import models.reverse.TournamentInfo
import models.round.{PlayoffRound, Round}
import models.tournament.GameRules
import modules.MockModule
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.test.WithApplication
import scaldi.{Module, Injectable}

import modules.DBMock._

class RoundDaoTest extends Specification with Injectable with Mockito {

  implicit val module = new Module { bind [RoundDao] to new RoundDaoImpl() } :: new MockModule

  lazy val roundDao = inject[RoundDao]
  lazy val unitDao = inject[UnitDao]

  val tournamentInfo = new TournamentInfo("t", None, GameRules(0, 1, 3))

  "Round.fromId test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val round = roundDao.fromId(1)

      //then
      round should beSome[Round]
      round.get.id.get shouldEqual 1
      round.get.units should beEmpty
      round.get.stepIndex shouldEqual 1

      round.get.pots should have size 2
      round.get.pots.head should have size 2
      round.get.pots.head.map(_.name) should containAllOf("Przemyśl" :: "Ustrzyki Dolne" :: Nil)

      round.get.pots.last should have size 2
      round.get.pots.last.map(_.name) should containAllOf("Rzeszów" :: "Ustrzyki Górne" :: Nil)

      round.get.teams should have size 4
      round.get.teams.map(_.name) should containAllOf("Przemyśl" :: "Rzeszów" :: "Ustrzyki Dolne" :: "Ustrzyki Górne" :: Nil)
    }
  }

  "Round saving test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      val r = new PlayoffRound("", List(city1, city2))(tournamentInfo)

      //when
      val rId = roundDao.saveOrUpdate(r, 1)

      //then
      val query = session.prepareStatement("SELECT id, class, step, is_preliminary, tournament_id FROM rounds WHERE rounds.id = ?")
      query.setLong(1, rId)
      val result = query.executeQuery()
      result.next()

      result.getLong(1) shouldEqual rId
      result.getString(2) shouldEqual classOf[PlayoffRound].getName
      result.getInt(3) shouldEqual 0
      result.getBoolean(4) should beFalse
      result.getLong(5) shouldEqual 1

      val citsQuery = session.prepareStatement("SELECT city_id, pot FROM rounds_cities WHERE rounds_cities.round_id = ? ORDER BY city_id ASC")
      citsQuery.setLong(1, rId)
      val citsResult = citsQuery.executeQuery()

      citsResult.next()
      citsResult.getLong(1) shouldEqual 1
      citsResult.getObject(2) should beNull
      citsResult.next()
      citsResult.getLong(1) shouldEqual 2
      citsResult.getObject(2) should beNull
      citsResult.next() should beFalse
    }
  }

  "Round updating test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val r = new PlayoffRound("", List(city1, city4), List(List(city1), List(city4)), Nil, 5, false, Some(1l))(tournamentInfo)

      //when
      val rId = roundDao.saveOrUpdate(r, 1)

      //then
      val query = session.prepareStatement("SELECT id, class, step, is_preliminary, tournament_id FROM rounds WHERE rounds.id = ?")
      query.setLong(1, rId)
      val result = query.executeQuery()
      result.next()

      result.getLong(1) shouldEqual rId
      result.getString(2) shouldEqual classOf[PlayoffRound].getName
      result.getInt(3) shouldEqual 5
      result.getBoolean(4) should beFalse
      result.getLong(5) shouldEqual 1

      val citsQuery = session.prepareStatement("SELECT city_id, pot FROM rounds_cities WHERE rounds_cities.round_id = ? ORDER BY city_id ASC")
      citsQuery.setLong(1, rId)
      val citsResult = citsQuery.executeQuery()

      citsResult.next()
      citsResult.getLong(1) shouldEqual 1
      citsResult.getLong(2) shouldEqual 0
      citsResult.next()
      citsResult.getLong(1) shouldEqual 4
      citsResult.getLong(2) shouldEqual 1
      citsResult.next() should beFalse
    }
  }

  "Round.fromId returns None when there is no such a round" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)

      //when
      val round = roundDao.fromId(1)

      //then
      round should beEmpty
    }
  }

  "Round tournaments rounds test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val rounds = roundDao.getTournamentRounds(1)

      //then
      rounds should have size 1
      rounds.map(_.name) should contain("round")
    }
  }
}