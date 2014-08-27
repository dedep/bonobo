package models.db_model

import org.specs2.mutable.Specification
import play.api.test.WithApplication

class TerritoryDaoTest extends Specification {
  "Territory.fromId returns proper Territory" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (3, 'Poland', 1, NULL);")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, 3);")

      //when
      val territory = Territory.fromId(1)

      //then
      assert(territory.nonEmpty)
      assert(territory.get.id === 1)
      assert(territory.get.name === "Podkarpackie")
      assert(territory.get.population === 2129951)

      assert(territory.get.container.nonEmpty)
      assert(territory.get.container.get.id === 3)
      assert(territory.get.container.get.name === "Poland")
      assert(territory.get.container.get.population === 1)
      assert(territory.get.container.get.container.isEmpty)
    }
  }

  "Territory.getDirectCities returns cities within territory" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE cities CASCADE;")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL);")

      session.createStatement().executeUpdate("INSERT INTO cities(name, population, container) VALUES ('Rzeszów', 182028, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities(name, population, container) VALUES ('Przemyśl', 64276, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities(name, population, container) VALUES ('Stalowa Wola', 64189, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities(name, population, container) VALUES ('Mielec', 61238, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities(name, population, container) VALUES ('Tarnobrzeg', 48558, 1);")

      //when
      val directCities = Territory.getDirectCities(Territory.fromId(1).get)

      //then
      assert(directCities.size === 5)
      assert(directCities.exists(_.name == "Rzeszów"))
      assert(directCities.exists(_.name == "Przemyśl"))
      assert(directCities.exists(_.name == "Stalowa Wola"))
      assert(directCities.exists(_.name == "Mielec"))
      assert(directCities.exists(_.name == "Tarnobrzeg"))
    }
  }

  "Territory.get returns all cities within territory including nested territories" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE cities CASCADE;")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (4, 'World', 1000000, NULL);")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (3, 'Europe', 1000000, 4);")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (2, 'Poland', 1, 3);")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, 2);")

      session.createStatement().executeUpdate("INSERT INTO cities(name, population, container) VALUES ('New York', 48558, 4);")
      session.createStatement().executeUpdate("INSERT INTO cities(name, population, container) VALUES ('Budapest', 48558, 3);")
      session.createStatement().executeUpdate("INSERT INTO cities(name, population, container) VALUES ('Warsaw', 61238, 2);")
      session.createStatement().executeUpdate("INSERT INTO cities(name, population, container) VALUES ('Rzeszów', 182028, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities(name, population, container) VALUES ('Przemyśl', 64276, 1);")
      session.createStatement().executeUpdate("INSERT INTO cities(name, population, container) VALUES ('Stalowa Wola', 64189, 1);")

      //when
      val directCities = Territory.getAllChildrenCities(Territory.fromId(3).get)

      //then
      assert(directCities.size === 5)
      assert(directCities.exists(_.name == "Rzeszów"))
      assert(directCities.exists(_.name == "Przemyśl"))
      assert(directCities.exists(_.name == "Stalowa Wola"))
      assert(directCities.exists(_.name == "Warsaw"))
      assert(directCities.exists(_.name == "Budapest"))
    }
  }

  "Territory updating test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL);")

      val t1 = new Territory(1, "Mazowieckie", 1000, None)

      //when
      Territory.update(t1)

      //then
      val query = session.prepareStatement("SELECT id, name, population, container FROM territories WHERE territories.id = ?")
      query.setLong(1, 1)
      val result = query.executeQuery()
      result.next()

      assert(result.getLong(1) === 1)
      assert(result.getString(2) === "Mazowieckie")
      assert(result.getLong(3) === 1000)
      assert(result.getObject(4) === null)
    }
  }

  "Cascade territory updating test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (2, 'Polska', 2129951, NULL);")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, 1);")

      val t2 = new Territory(2, "Wielkopolskie", 10000, None)
      val t1 = new Territory(1, "Mazowieckie", 1000, Some(t2))

      //when
      Territory.update(t1)

      //then
      val query = session.prepareStatement("SELECT id, name, population, container FROM territories WHERE territories.id = ?")

      query.setLong(1, 1)
      val result = query.executeQuery()
      result.next()
      assert(result.getLong(1) === 1)
      assert(result.getString(2) === "Mazowieckie")
      assert(result.getLong(3) === 1000)
      assert(result.getObject(4) === 2)

      query.setLong(1, 2)
      val result2 = query.executeQuery()
      result2.next()
      assert(result2.getLong(1) === 2)
      assert(result2.getString(2) === "Wielkopolskie")
      assert(result2.getLong(3) === 10000)
      assert(result2.getObject(4) === null)
    }
  }
}
