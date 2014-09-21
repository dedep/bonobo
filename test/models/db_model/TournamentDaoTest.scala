package models.db_model

import models.core.round.pair.{Pair, PlayoffRound}
import models.core.tournament.TournamentImpl
import org.specs2.mutable.Specification
import play.api.test.WithApplication

class TournamentDaoTest extends Specification {

  "Save tournament test cascade updates cities - no rounds" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")

      val tr = Territory.fromId(1).get
      val c1 = new City(1, "RzeszówEDIT", 182028, 0, tr, 0, 0)
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
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'Test tournament');")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")

      val tr = Territory.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = new City(2, "Poznań", 2, 0, tr, 0, 0)
      val t = new TournamentImpl(List(c1, c2), "New tournament", id = Some(1))

      //when
      val tournamentId = Tournament.saveOrUpdate(t)

      //then
      val tournamentQuery = session.prepareStatement("SELECT id, name FROM tournaments WHERE tournaments.id = ?")
      tournamentQuery.setLong(1, tournamentId)
      val trResult = tournamentQuery.executeQuery()
      trResult.next()
      assert(trResult.getLong(1) === tournamentId)
      assert(trResult.getString(2) === "New tournament")
    }
  }

  "Update tournament test - no rounds" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'Test tournament');")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")

      val tr = Territory.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = new City(2, "Poznań", 2, 0, tr, 0, 0)
      val t = new TournamentImpl(List(c1, c2), "New tournament", id = Some(1))

      //when
      Tournament.saveOrUpdate(t)

      //then
      val query = session.prepareStatement("SELECT id, name, population, points, container FROM cities WHERE cities.id = ?")
      query.setLong(1, 2)
      val result = query.executeQuery()
      result.next()
      assert(result.getLong(1) === 2)
      assert(result.getString(2) === "Poznań")
      assert(result.getLong(3) === 2)
      assert(result.getLong(4) === 0)
      assert(result.getLong(5) === 1)
    }
  }

  "Tournament.fromId returns proper Tournament - no rounds" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")

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
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")

      val tr = Territory.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = new City(2, "Lublin", 1600000, 100, tr, 0, 0)
      val t = new TournamentImpl(List(c1, c2), "New tournament")

      Tournament.saveOrUpdate(t) must throwA(new IllegalStateException("Tournament cannot refer to non-existent city"))
    }
  }

  "Save tournament test - no rounds" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")

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

  "Tournament saving test with all dependencies" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (3, 'Ustrzyki Dolne', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (4, 'Ustrzyki Górne', 61276, 0, 1, 0, 0);")

      val tr = Territory.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = City.fromId(2).get
      val c3 = City.fromId(3).get
      val c4 = City.fromId(4).get

      val u1 = new Pair("P1", List(c1, c4))
      val u2 = new Pair("P1", List(c2, c3))
      val r = new PlayoffRound("", List(c1, c2, c3, c4), List(List(c1, c3), List(c2, c4)), List(u1, u2))
      val t = new TournamentImpl(List(c1, c2, c3, c4), "New tournament", List(r))

      //when
      val tournamentId = Tournament.saveOrUpdate(t)
      val nt = Tournament.fromId(tournamentId).get

      //then
      //todo: przejście na czysto specsowe metody
      assert(nt.name === "New tournament")
      assert(nt.teams.size === 4)
      assert(nt.rounds.size === 1)
      assert(nt.rounds(0).stepIndex === 0)
      assert(nt.rounds(0).pots.size === 2)
      assert(nt.rounds(0).teams.size === 4)
      assert(nt.rounds(0).units.size === 2)

      nt.rounds(0).units(0).teams should have size 2
      nt.rounds(0).units(0).results should have size 2
      nt.rounds(0).units(0).fixtures should have size 2
      nt.rounds(0).units(0).fixtures(0) should have size 1
      List(nt.rounds(0).units(0).fixtures(0)(0).aTeam.name) must containAnyOf(List("Rzeszów", "Ustrzyki Górne"))
      List(nt.rounds(0).units(0).fixtures(0)(0).bTeam.name) must containAnyOf(List("Rzeszów", "Ustrzyki Górne"))
    }
  }
}