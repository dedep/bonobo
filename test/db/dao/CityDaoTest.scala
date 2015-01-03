package db.dao

import db.dao.city.{CityDao, CityDaoImpl}
import db.dao.territory.TerritoryDao
import models.territory.{Territory, City}
import modules.MockModule
import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.test._

import scaldi.{Module, Injectable}

import modules.DBMock._

class CityDaoTest extends Specification with Injectable with Mockito {

  implicit val module = new Module { bind [CityDao] to new CityDaoImpl() } :: new MockModule

  lazy val territoryDao = inject[TerritoryDao]
  lazy val cityDao = inject[CityDao]

  "City.fromId returns proper City" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val city = cityDao.fromId(2)

      //then
      city should beSome[City]
      city.get.id shouldEqual 2
      city.get.name shouldEqual "Koszalin"
      city.get.points shouldEqual 0
      city.get.population shouldEqual 64276
      city.get.territory.id shouldEqual 1
    }
  }

  "City.fromId returns None when there is no such a city" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables

      //when
      val city = cityDao.fromId(2)

      //then
      city should beEmpty
    }
  }

  "City saving test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      val c1 = new City(1, "Warszawa", 1000, 2, ter1, 0, 0)

      //when
      val cityId = cityDao.saveOrUpdate(c1)

      //then
      val query = session.prepareStatement("SELECT id, name, population, points, container FROM cities WHERE cities.id = ?")
      query.setLong(1, cityId)
      val result = query.executeQuery()
      result.next()

      result.getLong(1) shouldEqual cityId
      result.getString(2) shouldEqual "Warszawa"
      result.getLong(3) shouldEqual 1000
      result.getLong(4) shouldEqual 2
      result.getLong(5) shouldEqual 1
    }
  }

  "Cannot save city referring to non-existent territory test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.truncateTestTables
      val t1 = new Territory(99, "Non", 2222, None, "NN", false, false)
      val c1 = new City(1, "Warszawa", 1000, 2, t1, 0, 0)

      //when - then
      cityDao.saveOrUpdate(c1) must throwA(new IllegalStateException("City cannot refer to non-existent territory"))
    }
  }

  "City updating test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      val c1 = new City(2, "Warszawa", 1000, 2, ter1, 0, 0)

      //when
      cityDao.saveOrUpdate(c1)

      //then
      val query = session.prepareStatement("SELECT id, name, population, points, container FROM cities WHERE cities.id = ?")
      query.setLong(1, 2)
      val result = query.executeQuery()
      result.next()

      assert(result.getLong(1) === 2)
      assert(result.getString(2) === "Warszawa")
      assert(result.getLong(3) === 1000)
      assert(result.getLong(4) === 2)
      assert(result.getLong(5) === 1)
    }
  }

  "City.getDirectCities returns all cities within territory" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val directCities = cityDao.getAllWithinTerritory(ter2)

      //then
      directCities should have size 5
      directCities.map(_.name) should containAllOf("Rzeszów" :: "Przemyśl" :: "Stalowa Wola" :: "Mielec" :: "Tarnobrzeg" :: Nil)
    }
  }

  "Returns all cities within territory including nested territories" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val cities = cityDao.getAllWithinTerritoryCascade(ter4)

      //then
      cities should have size 7
      cities.map(_.name) should containAllOf("Rzeszów" :: "Przemyśl" :: "Stalowa Wola" :: "Mielec" :: "Tarnobrzeg" :: "Warszawa" :: "Budapeszt" :: Nil)
      cities.map(_.name) should not contain "Nowy Jork"
    }
  }

}