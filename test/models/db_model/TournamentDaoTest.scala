package models.db_model

import models.core.tournament.TournamentImpl
import org.specs2.mutable.Specification
import play.api.test.WithApplication

class TournamentDaoTest extends Specification {
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

  "Saving Tournament test - no rounds" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
//      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
//      session.createStatement().executeUpdate("TRUNCATE cities CASCADE;")
//      session.createStatement().executeUpdate("TRUNCATE tournaments CASCADE;")
//      session.createStatement().executeUpdate("TRUNCATE cities_tournaments CASCADE;")
//
//      val tr = new Territory(1, "Polska", 36000000, None)
//      val c1 = new City(1, "Warszawa", 1600000, 100, tr)
//      val c2 = new City(1, "Lublin", 1600000, 100, tr)
//      val t = new TournamentImpl(List(c1, c2), "New tournament")
//
      //when
//      val tournamentId = Tournament.saveOrUpdate(t)

      //then
//      assert(tournamentId.nonEmpty)
    }
  }
}