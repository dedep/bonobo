package models.db_model

import org.specs2.mutable._
import play.api.test._

class CityDaoTest extends Specification {
    "City.fromId returns proper City" in new WithApplication {
      play.api.db.slick.DB("test").withSession { implicit session =>
        //given
        session.createStatement().executeUpdate("TRUNCATE cities CASCADE;")
        session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
        session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL);")
        session.createStatement().executeUpdate("INSERT INTO cities VALUES(2, 'Przemyśl', 64276, 0, 1);")

        //when
        val city = City.fromId(2)

        //then
        assert(city.nonEmpty)
        assert(city.get.id === 2)
        assert(city.get.name === "Przemyśl")
        assert(city.get.points === 0)
        assert(city.get.population === 64276)

        assert(city.get.territory.id === 1)
      }
    }

  "City.fromId returns None when there is no such a city" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      session.createStatement().executeUpdate("TRUNCATE cities CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")

      //when
      val city = City.fromId(2)

      //then
      assert(city.isEmpty)
    }
  }

  "City saving test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      session.createStatement().executeUpdate("TRUNCATE cities CASCADE;")
      session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
      session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, NULL);")

      val terr = Territory.fromId(1)
      val c1 = new City(1, "Warszawa", 1000, 2, terr.get)

      //when
      val cityId = City.saveOrUpdate(c1)

      //then
      val query = session.prepareStatement("SELECT id, name, population, points, container FROM cities WHERE cities.id = ?")
      query.setLong(1, cityId)
      val result = query.executeQuery()
      result.next()

      assert(result.getLong(1) === cityId)
      assert(result.getString(2) === "Warszawa")
      assert(result.getLong(3) === 1000)
      assert(result.getLong(4) === 2)
      assert(result.getLong(5) === 1)
    }
  }
}