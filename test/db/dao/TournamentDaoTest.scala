package db.dao

import db.dao.city.CityDao
import db.dao.round.RoundDao
import db.dao.territory.TerritoryDao
import db.dao.tournament.{TournamentDao, TournamentDaoImpl}
import models.territory.{Territory, City}
import models.tournament.{GameRules, Tournament, TournamentImpl}
import modules.{MockModule, DBMock}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.test.WithApplication
import scaldi.{Module, Injectable}

import DBMock._

class TournamentDaoTest extends Specification with Injectable with Mockito {

  implicit val module = new Module { bind [TournamentDao] to new TournamentDaoImpl() } :: new MockModule

  lazy val tournamentDao = inject[TournamentDao]
  lazy val cityDao = inject[CityDao]
  lazy val territoryDao = inject[TerritoryDao]
  lazy val roundDao = inject[RoundDao]

  val rules = GameRules(0, 1, 3)

  "Call SaveOrUpdate cities while saving new tournament" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val c1 = cityDao.fromId(1).get
      val c2 = cityDao.fromId(2).get
      val tr = new Territory(1, "test-territory", 22, None, "CD", false, false)
      val t = new TournamentImpl(tr, List(c1, c2), "New tournament")(rules)

      //when
      tournamentDao.updateLastRound(t)

      //then
      there was one(cityDao).update(c1)
      there was one(cityDao).update(c2)
    }
  }

  "Update tournament table" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val tr = new Territory(1, "test-territory", 22, None, "CD", false, false)
      val t = new TournamentImpl(tr, List(city1, city2), "New tournament", id = Some(1))(rules)

      //when
      val tournamentId = tournamentDao.updateLastRound(t)

      //then
      val tournamentQuery = session.prepareStatement("SELECT id, name FROM tournaments WHERE tournaments.id = ?")
      tournamentQuery.setLong(1, tournamentId)
      val trResult = tournamentQuery.executeQuery()
      trResult.next()

      trResult.getLong(1) shouldEqual tournamentId
      trResult.getString(2) shouldEqual "New tournament"
    }
  }

  "Call SaveOrUpdate cities while updating tournament" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val tr = new Territory(1, "test-territory", 22, None, "CD", false, false)
      val t = new TournamentImpl(tr, List(city1, city2), "New tournament", id = Some(1))(rules)

      //when
      tournamentDao.updateLastRound(t)

      //then
      there was one(cityDao).update(city1)
      there was one(cityDao).update(city2)
    }
  }

  "Find proper tournament" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val tournament = tournamentDao.fromId(1)

      //then
      tournament should beSome[Tournament]
      tournament.get.id.get shouldEqual 1
      tournament.get.name shouldEqual "DB Test tournament"
      tournament.get.rounds should beEmpty

      tournament.get.teams should have size 4
      tournament.get.teams must containAllOf(List(city1, city2, city3, city4))
    }
  }

  "Cannot save tournament that refers to non-existent city test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")

      val c2 = new City(99, "Lublin", 1600000, 100, ter1, 0, 0)
      val tr = new Territory(1, "test-territory", 22, None, "CD", false, false)
      val t = new TournamentImpl(tr, List(city1, c2), "New tournament")(rules)

      //when - then
      tournamentDao.updateLastRound(t) must throwA(new IllegalStateException("Tournament cannot refer to non-existent city"))
    }
  }

  "Save tournament test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      val tr = new Territory(1, "test-territory", 22, None, "CD", false, false)
      val t = new TournamentImpl(tr, List(city1, city2), "New tournament")(rules)

      //when
      val tournamentId = tournamentDao.updateLastRound(t)

      //then
      val tournamentQuery = session.prepareStatement("SELECT id, name FROM tournaments WHERE tournaments.id = ?")
      tournamentQuery.setLong(1, tournamentId)
      val trResult = tournamentQuery.executeQuery()
      trResult.next()
      trResult.getLong(1) shouldEqual tournamentId
      trResult.getString(2) shouldEqual  "New tournament"

      val citsTournamentsQuery = session.prepareStatement("SELECT city_id FROM cities_tournaments WHERE tournament_id = ? ORDER BY city_id ASC")
      citsTournamentsQuery.setLong(1, tournamentId)
      val ctrResult = citsTournamentsQuery.executeQuery()
      ctrResult.next()
      ctrResult.getLong(1) shouldEqual city1.id
      ctrResult.next()
      ctrResult.getLong(1) shouldEqual city2.id
      ctrResult.next() should beFalse
    }
  }

  //todo: TEST should call Round.saveOrUpdate
}