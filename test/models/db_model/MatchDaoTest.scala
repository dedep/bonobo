package models.db_model

import models.core._match.PlayedMatch
import models.core._match.result.{Draw, WinB}
import models.core.round.pair.Pair
import org.specs2.mutable.Specification
import play.api.test.WithApplication

class MatchDaoTest extends Specification {

  "Match.fromId returns proper Match" in new WithApplication {
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
      session.createStatement().executeUpdate("INSERT INTO matches VALUES (2, 1, 0, 3, 5, 4, 9);")
      session.createStatement().executeUpdate("INSERT INTO matches VALUES (3, 1, 1, 1, 2, 1, 5);")
      session.createStatement().executeUpdate("INSERT INTO matches VALUES (4, 1, 1, 3, 5, 4, 10);")

      //when
      val u = Unit.fromId(1).get
      val m = Match.fromId(2)

      //then
      assert(m.nonEmpty)
      assert(m.get.isInstanceOf[PlayedMatch])
      assert(m.get.id.get === 2)

      val pm = m.get.asInstanceOf[PlayedMatch]

      assert(pm.aTeam.name === "Jasło")
      assert(pm.bTeam.name === "Ustrzyki Dolne")

      assert(pm.result.aGoals === 5)
      assert(pm.result.bGoals === 9)

      assert(u.fixturesCount === 2)
      assert(u.fixtures(0)(0).id.get === 1)
      assert(u.fixtures(0)(1).id.get === 2)
      assert(u.fixtures(1)(0).id.get === 3)
      assert(u.fixtures(1)(1).id.get === 4)
    }
  }

  "Match.fromId returns None when there is no such a match" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables(session)

      //when
      val unit = Match.fromId(1222)

      //then
      assert(unit.isEmpty)
    }
  }

  "Unplayed match saving test" in new WithApplication {
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
      session.createStatement().executeUpdate("INSERT INTO units VALUES (9, 1, 'models.core.round.pair.Pair');")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (1, 9, 0, 0, 2);")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (2, 9, 0, 0, 0);")

      val c1 = City.fromId(1).get
      val c2 = City.fromId(2).get
      val u = Unit.fromId(9).get
      val m = new models.core._match.Match(c1, c2)

      //when
      val mId = Match.saveOrUpdate(m, 1, u.id.get)

      //then
      val query = session.prepareStatement("SELECT id, unit_id, fixture, a_team_id, a_team_goals, b_team_id, b_team_goals FROM matches WHERE id = ?")
      query.setLong(1, mId)
      val result = query.executeQuery()
      result.next()

      assert(result.getLong(1) === mId)
      assert(result.getLong(2) === 9)
      assert(result.getInt(3) === 1)
      assert(result.getLong(4) === 1)
      assert(result.getObject(5) === null)
      assert(result.getLong(6) === 2)
      assert(result.getObject(7) === null)
      assert(!result.next())
    }
  }

  "Played match saving test" in new WithApplication {
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
      session.createStatement().executeUpdate("INSERT INTO units VALUES (9, 1, 'models.core.round.pair.Pair');")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (1, 9, 0, 0, 2);")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (2, 9, 0, 0, 0);")

      val c1 = City.fromId(1).get
      val c2 = City.fromId(2).get
      val u = Unit.fromId(9).get
      val m = new PlayedMatch(c1, c2, WinB(2, 4))

      //when
      val mId = Match.saveOrUpdate(m, 1, u.id.get)

      //then
      val query = session.prepareStatement("SELECT id, unit_id, fixture, a_team_id, a_team_goals, b_team_id, b_team_goals FROM matches WHERE id = ?")
      query.setLong(1, mId)
      val result = query.executeQuery()
      result.next()

      assert(result.getLong(1) === mId)
      assert(result.getLong(2) === 9)
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
      session.createStatement().executeUpdate("INSERT INTO units VALUES (9, 1, 'models.core.round.pair.Pair');")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (1, 9, 0, 0, 2);")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (2, 9, 0, 0, 0);")
      session.createStatement().executeUpdate("INSERT INTO matches VALUES (7, 9, 0, 1, null, 1, null);")

      val tr = Territory.fromId(1).get
      val c1 = City.fromId(1).get
      val c2 = new City(2, "Krosno", 222, 4, tr, 0, 0)
      val u = Unit.fromId(9).get
      val m = new PlayedMatch(c1, c2, Draw(2), Some(7))

      //when
      Match.saveOrUpdate(m, 0, u.id.get)

      //then
      val query = session.prepareStatement("SELECT id, unit_id, fixture, a_team_id, a_team_goals, b_team_id, b_team_goals FROM matches WHERE id = ?")
      query.setLong(1, 7)
      val result = query.executeQuery()
      result.next()

      assert(result.getLong(1) === 7)
      assert(result.getLong(2) === 9)
      assert(result.getInt(3) === 0)
      assert(result.getLong(4) === 1)
      assert(result.getInt(5) === 2)
      assert(result.getLong(6) === 2)
      assert(result.getInt(7) === 2)
      assert(!result.next())

      val mQuery = session.prepareStatement("SELECT id, name, population, points, container FROM cities WHERE cities.id = ?")
      mQuery.setLong(1, 2)
      val mResult = mQuery.executeQuery()
      mResult.next()

      assert(mResult.getLong(1) === 2)
      assert(mResult.getString(2) === "Krosno")
      assert(mResult.getLong(3) === 222)
      assert(mResult.getLong(4) === 4)
      assert(mResult.getLong(5) === 1)
    }
  }

  "Match cannot contain cities that are not in parent Unit exception test" in new WithApplication {
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
      session.createStatement().executeUpdate("INSERT INTO units VALUES (9, 1, 'models.core.round.pair.Pair');")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (1, 9, 0, 0, 2);")
      session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (2, 9, 0, 0, 0);")

      val c1 = City.fromId(1).get
      val c2 = new City(3, "Sanok", 23232, 0, Territory.fromId(1).get, 0, 0)
      val m = new models.core._match.Match(c1, c2)
      val u = Unit.fromId(9).get

      //when then
      Match.saveOrUpdate(m, 0, u.id.get) must throwA(new IllegalStateException("Match cannot contain cities that are not from parent unit"))
    }
  }

  "Match cannot contain refer to non-existent Unit exception test" in new WithApplication {
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
      val c2 = new City(3, "Sanok", 23232, 0, Territory.fromId(1).get, 0, 0)
      val m = new models.core._match.Match(c1, c2)

      //when then
      Match.saveOrUpdate(m, 0, 99) must throwA(new IllegalStateException("Match cannot refer to non-existent unit"))
    }
  }
}