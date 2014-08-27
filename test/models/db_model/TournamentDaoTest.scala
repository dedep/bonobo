package models.db_model

import models.core.tournament.TournamentImpl
import org.specs2.mutable.Specification
import play.api.test.WithApplication

class TournamentDaoTest extends Specification {

  "Save tournament test cascade updates cities - no rounds" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE cities CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE tournaments CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE cities_tournaments CASCADE;")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1);")

      val tr = Territory.fromId(1).get
      val c1 = new City(1, "RzeszówEDIT", 182028, 0, tr)
      val c2 = City.fromId(2).get
      val t = new TournamentImpl(List(c1, c2), "New tournament")

      //when
      Tournament.saveOrUpdate(t)

      //then
      val query = session.prepareStatement("SELECT id, name, population, points, container FROM cities WHERE cities.id = ?")
      query.setLong(1, 1)
      val result = query.executeQuery()
      result.next()

      assert(result.getLong(1) === 1)
      assert(result.getString(2) === "RzeszówEDIT")
    }
  }

  "Update tournament test - no rounds" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE cities CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE tournaments CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE cities_tournaments CASCADE;")

      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1);")
      session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'Test tournament');")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")

      val tr = Territory.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = City.fromId(2).get
      val t = new TournamentImpl(List(c1, c2), "New tournament", id = Some(1))

      //when
      val tournamentId = Tournament.saveOrUpdate(t)

      //then
//      val tournamentQuery = session.prepareStatement("SELECT id, name FROM tournaments WHERE tournaments.id = ?")
//      tournamentQuery.setLong(1, tournamentId)
//      val trResult = tournamentQuery.executeQuery()
//      trResult.next()
//      assert(trResult.getLong(1) === tournamentId)
//      assert(trResult.getString(2) === "New tournament")
//
//      val citsTournamentsQuery = session.prepareStatement("SELECT city_id FROM cities_tournaments WHERE tournament_id = ? ORDER BY city_id ASC")
//      citsTournamentsQuery.setLong(1, tournamentId)
//      val ctrResult = citsTournamentsQuery.executeQuery()
//      ctrResult.next()
//      assert(ctrResult.getLong(1) === c1.id)
//      ctrResult.next()
//      assert(ctrResult.getLong(1) === c2.id)
//      assert(ctrResult.next() === false)
    }
  }

  "Tournament.fromId returns proper Tournament - no rounds" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE cities CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE tournaments CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE cities_tournaments CASCADE;")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1);")

      session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'Test tournament');")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")

      //when
      val tournament = Tournament.fromId(1)

      //then
      assert(tournament.nonEmpty)
      assert(tournament.get.id.get === 1)
      assert(tournament.get.name === "Test tournament")
      assert(tournament.get.rounds === Nil)

      assert(tournament.get.teams.size === 2)
      assert(tournament.get.teams.exists(_.name == "Rzeszów") === true)
      assert(tournament.get.teams.exists(_.name == "Przemyśl") === true)
    }
  }

  "Cannot save tournament that refers to non-existent city test - no rounds" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE cities CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE tournaments CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE cities_tournaments CASCADE;")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1);")

      val tr = Territory.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = new City(2, "Lublin", 1600000, 100, tr)
      val t = new TournamentImpl(List(c1, c2), "New tournament")

      Tournament.saveOrUpdate(t) must throwA(new IllegalStateException("Tournament cannot refer to non-existent city"))
    }
  }

  "Save tournament test - no rounds" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE cities CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE tournaments CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE cities_tournaments CASCADE;")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1);")

      val tr = Territory.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = City.fromId(2).get
      val t = new TournamentImpl(List(c1, c2), "New tournament")

      //when
      val tournamentId = Tournament.saveOrUpdate(t)

      //then
      val tournamentQuery = session.prepareStatement("SELECT id, name FROM tournaments WHERE tournaments.id = ?")
      tournamentQuery.setLong(1, tournamentId)
      val trResult = tournamentQuery.executeQuery()
      trResult.next()
      assert(trResult.getLong(1) === tournamentId)
      assert(trResult.getString(2) === "New tournament")

      val citsTournamentsQuery = session.prepareStatement("SELECT city_id FROM cities_tournaments WHERE tournament_id = ? ORDER BY city_id ASC")
      citsTournamentsQuery.setLong(1, tournamentId)
      val ctrResult = citsTournamentsQuery.executeQuery()
      ctrResult.next()
      assert(ctrResult.getLong(1) === c1.id)
      ctrResult.next()
      assert(ctrResult.getLong(1) === c2.id)
      assert(ctrResult.next() === false)
    }
  }
}