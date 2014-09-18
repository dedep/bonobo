package models.db_model

import models.core._match.PlayedMatch
import models.core._match.result.Draw
import models.core.round.group.Group
import models.core.round.pair.{PlayoffRound, Pair}
import models.core.round.result.TeamResult
import org.specs2.mutable.Specification
import play.api.test.WithApplication

class UnitDaoTest extends Specification {

  "Unit.fromId returns proper Unit" in new WithApplication {
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
      session.createStatement().executeUpdate("INSERT INTO units VALUES (1, 1, 'models.core.round.group.Group');")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (1, 1, 0, 0, 2);")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (2, 1, 0, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (3, 1, 0, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (4, 1, 0, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO matches VALUES (1, 1, 0, 1, 2, 1, 4);")

      //when
      val unit = Unit.fromId(1)

      //then
      assert(unit.nonEmpty)
      assert(unit.get.isInstanceOf[Group])
      assert(unit.get.id.get === 1)

      assert(unit.get.fixturesCount === 1)
      assert(unit.get.results.exists(tr => tr.goalsConceded == 2 && tr.goalsScored == 0 && tr.team.name == "Rzeszów" && tr.points == 0))
      assert(unit.get.results.exists(_.team.name == "Ustrzyki Dolne"))
      assert(unit.get.results.exists(_.team.name == "Rzeszów"))
      assert(unit.get.results.exists(_.team.name == "Przemyśl"))

      assert(unit.get.teams.exists(_.name == "Rzeszów"))
      assert(unit.get.teams.exists(_.name == "Ustrzyki Dolne"))
      assert(unit.get.teams.exists(_.name == "Rzeszów"))
      assert(unit.get.teams.exists(_.name == "Przemyśl"))
    }
  }

  "Unit.fromId returns None when there is no such a unit" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)

      //when
      val unit = Unit.fromId(1)

      //then
      assert(unit.isEmpty)
    }
  }

  "Unit saving test - no fixtures" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'Test tournament');")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")
      session.createStatement().executeUpdate("INSERT INTO rounds VALUES (1, 'round', 'models.core.round.pair.PlayoffRound', 1, false, 1);")
      session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 1, 1);")
      session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 2, 0);")

      val tr = Tournament.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = City.fromId(2).get
      val r = Round.fromId(1).get
      val u = new Pair(List(c1, c2), resultsCbn = List(TeamResult(c1, 1, 2, 3), TeamResult(c2, 4, 5, 6)))

      //when
      val uId = Unit.saveOrUpdate(u, r.id.get)

      //then
      val query = session.prepareStatement("SELECT u.id, u.round_id, u.class, uc.city_id, uc.points, uc.goals_scored, uc.goals_conceded" +
        " FROM units u LEFT JOIN units_cities uc ON u.id = uc.unit_id WHERE u.id = ?")
      query.setLong(1, uId)
      val result = query.executeQuery()
      result.next()

      assert(result.getLong(1) === uId)
      assert(result.getLong(2) === 1)
      assert(result.getString(3) === classOf[Pair].getName)
      assert(result.getInt(4) === 1)
      assert(result.getInt(5) === 1)
      assert(result.getInt(6) === 2)
      assert(result.getInt(7) === 3)

      result.next()
      assert(result.getInt(4) === 2)
      assert(result.getInt(5) === 4)
      assert(result.getInt(6) === 5)
      assert(result.getInt(7) === 6)
      assert(!result.next())
    }
  }

  "Unit updating test - no fixtures" in new WithApplication {
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
      session.createStatement().executeUpdate("INSERT INTO units VALUES (1, 1, 'models.core.round.group.Group');")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (1, 1, 0, 0, 2);")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (2, 1, 0, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (3, 1, 0, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (4, 1, 0, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO matches VALUES (1, 1, 0, 1, 2, 1, 4);")

      val t = Territory.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = City.fromId(2).get
      val c3 = City.fromId(3).get
      val c4 = new City(4, "Tarnów", 222, 4, t, 0, 0)
      val r = Round.fromId(1).get
      val u = new Group(List(c1, c2, c3, c4), Nil, List(TeamResult(c1, 6, 3, 5), TeamResult(c2, 2, 3, 4), TeamResult(c3, 4, 1, 2), TeamResult(c4, 1, 1, 1)), Some(1))

      //when
      Unit.saveOrUpdate(u, r.id.get)

      //then
      val query = session.prepareStatement("SELECT u.id, u.round_id, u.class, uc.city_id, uc.points, uc.goals_scored, uc.goals_conceded" +
        " FROM units u LEFT JOIN units_cities uc ON u.id = uc.unit_id WHERE u.id = ?")
      query.setLong(1, 1)
      val result = query.executeQuery()
      result.next()

      assert(result.getLong(1) === 1)
      assert(result.getLong(2) === 1)
      assert(result.getString(3) === classOf[Group].getName)
      assert(result.getInt(4) === 1)
      assert(result.getInt(5) === 6)
      assert(result.getInt(6) === 3)
      assert(result.getInt(7) === 5)

      result.next()
      assert(result.getInt(4) === 2)
      assert(result.getInt(5) === 2)
      assert(result.getInt(6) === 3)
      assert(result.getInt(7) === 4)

      result.next()
      assert(result.getInt(4) === 3)
      assert(result.getInt(5) === 4)
      assert(result.getInt(6) === 1)
      assert(result.getInt(7) === 2)

      result.next()
      assert(result.getInt(4) === 4)
      assert(result.getInt(5) === 1)
      assert(result.getInt(6) === 1)
      assert(result.getInt(7) === 1)
      assert(!result.next())

      val citsQuery = session.prepareStatement("SELECT id, name, population, points, container FROM cities WHERE cities.id = ?")
      citsQuery.setLong(1, 4)
      val citsResult = citsQuery.executeQuery()
      citsResult.next()
      assert(citsResult.getLong(1) === 4)
      assert(citsResult.getString(2) === "Tarnów")
      assert(citsResult.getLong(3) === 222)
      assert(citsResult.getLong(4) === 4)
      assert(citsResult.getLong(5) === 1)
    }
  }

  "Unit cannot contain cities that are not in parent Round exception test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'Test tournament');")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")
      session.createStatement().executeUpdate("INSERT INTO rounds VALUES (1, 'round', 'models.core.round.pair.PlayoffRound', 1, false, 1);")
      session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 1, 1);")
      session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 2, 0);")

      val c1 = City.fromId(1).get
      val c2 = new City(3, "Sanok", 23232, 0, Territory.fromId(1).get, 0, 0)
      val u = new Pair(List(c1, c2))
      val r = Round.fromId(1).get

      //when then
      Unit.saveOrUpdate(u, r.id.get) must throwA(new IllegalStateException("Unit cannot contain cities that are not from parent round"))
    }
  }

  "Unit cannot contain refer to non-existent Round exception test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL, '');")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Rzeszów', 182028, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Przemyśl', 64276, 0, 1, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'Test tournament');")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")

      val c1 = City.fromId(1).get
      val c2 = City.fromId(2).get
      val u = new Pair(List(c1, c2))
      val r = new PlayoffRound("", List(c1, c2), Nil, Nil, 0, Some(8l))

      //when then
      Unit.saveOrUpdate(u, 8) must throwA(new IllegalStateException("Unit cannot refer to non-existent round"))
    }
  }
}
