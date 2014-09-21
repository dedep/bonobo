package models.db_model

import models.core.round.group.GroupRound
import models.core.round.pair.PlayoffRound
import org.specs2.mutable.Specification
import play.api.test.WithApplication

class RoundDaoTest extends Specification {

  "Round.fromId returns proper Round - no units" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (3, 'Jasło', 642761, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (4, 'Ustrzyki Dolne', 6421, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'Test tournament');")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (3, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (4, 1);")
      session.createStatement().executeUpdate("INSERT INTO rounds VALUES (1, 'round', 'models.core.round.group.GroupRound', 1, false, 1);")
      session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 1, 1);")
      session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 2, 0);")
      session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 3, 0);")
      session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 4, 1);")

      //when
      val round = Round.fromId(1)

      //then
      assert(round.nonEmpty)
      assert(round.get.isInstanceOf[GroupRound])
      assert(round.get.id.get === 1)
      assert(round.get.units === Nil)
      assert(round.get.stepIndex === 1)

      assert(round.get.pots.size === 2)
      assert(round.get.pots.head.size === 2)
      assert(round.get.pots.head.exists(_.name == "Przemyśl"))
      assert(round.get.pots.head.exists(_.name == "Jasło"))

      assert(round.get.pots.last.size === 2)
      assert(round.get.pots.last.exists(_.name == "Rzeszów"))
      assert(round.get.pots.last.exists(_.name == "Ustrzyki Dolne"))

      assert(round.get.teams.size === 4)
      assert(round.get.teams.exists(t => t.name == "Rzeszów"))
      assert(round.get.teams.exists(t => t.name == "Przemyśl"))
    }
  }

  "Round saving test - no units" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'Test tournament');")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")

      val tr = Tournament.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = City.fromId(2).get
      val r = new PlayoffRound("", List(c1, c2))

      //when
      val rId = Round.saveOrUpdate(r, tr.id.get)

      //then
      val query = session.prepareStatement("SELECT id, class, step, is_preliminary, tournament_id FROM rounds WHERE rounds.id = ?")
      query.setLong(1, rId)
      val result = query.executeQuery()
      result.next()

      assert(result.getLong(1) === rId)
      assert(result.getString(2) === classOf[PlayoffRound].getName)
      assert(result.getInt(3) === 0)
      assert(result.getBoolean(4) === false)
      assert(result.getLong(5) === 1)

      val citsQuery = session.prepareStatement("SELECT city_id, pot FROM rounds_cities WHERE rounds_cities.round_id = ? ORDER BY city_id ASC")
      citsQuery.setLong(1, rId)
      val citsResult = citsQuery.executeQuery()

      citsResult.next()
      assert(citsResult.getLong(1) === 1)
      assert(citsResult.getObject(2) === null)
      citsResult.next()
      assert(citsResult.getLong(1) === 2)
      assert(citsResult.getObject(2) === null)
      assert(!citsResult.next())
    }
  }

  "Round updating test - no units" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'Test tournament');")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")
      session.createStatement().executeUpdate("INSERT INTO rounds VALUES (1, 'round', 'models.core.round.pair.GroupRound', 1, false, 1);")

      val tr = Tournament.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = City.fromId(2).get
      val r = new PlayoffRound("", List(c1, c2), Nil, Nil, 5)

      //when
      val rId = Round.saveOrUpdate(r, tr.id.get)

      //then
      val query = session.prepareStatement("SELECT id, class, step, is_preliminary, tournament_id FROM rounds WHERE rounds.id = ?")
      query.setLong(1, rId)
      val result = query.executeQuery()
      result.next()

      assert(result.getLong(1) === rId)
      assert(result.getString(2) === classOf[PlayoffRound].getName)
      assert(result.getInt(3) === 5)
      assert(result.getBoolean(4) === false)
      assert(result.getLong(5) === 1)
    }
  }

  "Round updating updates pots and cities - no units" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (3, 'Jasło', 642761, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (4, 'Ustrzyki Dolne', 6421, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'Test tournament');")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (3, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (4, 1);")
      session.createStatement().executeUpdate("INSERT INTO rounds VALUES (1, 'round', 'models.core.round.pair.PlayoffRound', 1, false, 1);")
      session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 1, 7);")
      session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 2, 8);")
      session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 3, 7);")
      session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 4, 8);")

      val tr = Tournament.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = new City(2, "Sanok", 23232, 0, Territory.fromId(1).get, 0, 0)
      val c3 = City.fromId(3).get
      val c4 = City.fromId(4).get
      val r = new PlayoffRound("", List(c1, c2, c3, c4), List(List(c2, c3), List(c1, c4)), Nil, 0, false, Some(1))

      //when
      Round.saveOrUpdate(r, tr.id.get)

      //then
      val potsQuery = session.prepareStatement("SELECT city_id, pot FROM rounds_cities WHERE rounds_cities.round_id = ? ORDER BY city_id ASC")
      potsQuery.setLong(1, 1)
      val citsResult = potsQuery.executeQuery()
      citsResult.next()
      assert(citsResult.getLong(1) === 1)
      assert(citsResult.getLong(2) === 1)
      citsResult.next()
      assert(citsResult.getLong(1) === 2)
      assert(citsResult.getObject(2) === 0)
      citsResult.next()
      assert(citsResult.getLong(1) === 3)
      assert(citsResult.getObject(2) === 0)
      citsResult.next()
      assert(citsResult.getLong(1) === 4)
      assert(citsResult.getObject(2) === 1)
      assert(!citsResult.next())

//      val citsQuery = session.prepareStatement("SELECT id, name, population, points, container FROM cities WHERE cities.id = ?")
//      citsQuery.setLong(1, 2)
//      val result = citsQuery.executeQuery()
//      result.next()
//      assert(result.getLong(1) === 2)
//      assert(result.getString(2) === "Sanok")
//      assert(result.getLong(3) === 23232)
//      assert(result.getLong(4) === 0)
//      assert(result.getLong(5) === 1)
    }
  }

  "Round cannot refer to non-existing tournament exception test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")

      val c1 = City.fromId(1).get
      val c2 = City.fromId(2).get
      val r = new PlayoffRound("", List(c1, c2))

      //when then
      Round.saveOrUpdate(r, 1) must throwA(new IllegalStateException("Round cannot refer to non-existent tournament"))
    }
  }

  "Round cannot contain cities that are not in parent tournament exception test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'Test tournament');")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")

      val tr = Tournament.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = new City(3, "Sanok", 23232, 0, Territory.fromId(1).get, 0, 0)
      val r = new PlayoffRound("", List(c1, c2))

      //when then
      Round.saveOrUpdate(r, tr.id.get) must throwA(new IllegalStateException("Round cannot contain cities that are not from parent tournament"))
    }
  }

  "Round.fromId returns None when there is no such a round" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)

      //when
      val round = Round.fromId(1)

      //then
      assert(round.isEmpty)
    }
  }
}