package db.dao

import db.dao.CityDao
import db.dao.territory.TerritoryDaoImpl
import models.territory.Territory
import modules.MockModule
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.test.WithApplication
import scaldi.{Module, Injectable}

import modules.DBMock._

class TerritoryDaoTest extends Specification with Injectable with Mockito {

  implicit val module = new Module { bind [TerritoryDao] to new TerritoryDaoImpl() } :: new MockModule

  lazy val territoryDao = inject[TerritoryDao]
  lazy val cityDao = inject[CityDao]

  "Territory.find returns proper Territory" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val origin = ter1

      //when
      val territory = territoryDao.find(origin.code)

      //then
      territory should beSome[Territory]
      territory.get.code shouldEqual origin.code
      territory.get.name shouldEqual origin.name
      territory.get.population shouldEqual origin.population

      territory.get.container should beSome[Territory]
      territory.get.container.get.code shouldEqual origin.container.get.code
      territory.get.container.get.name shouldEqual origin.container.get.name
      territory.get.container.get.population shouldEqual origin.container.get.population
    }
  }

  "Territory.findAll returns all territories" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val territories = territoryDao.findAll

      //then
      territories should have size 5
      territories should containAllOf(ter1 :: ter2 :: ter3 :: ter4 :: ter5 :: Nil)
    }
  }

  "Territory updating test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val t1 = new Territory("PLMZ", "Mazowieckie", 1000, None, false, false)

      //when
      territoryDao.update(t1, "PLLU")

      //then
      val query = session.prepareStatement("SELECT code, name, population, container FROM territories WHERE territories.code = ?")
      query.setString(1, "PLMZ")
      val result = query.executeQuery()
      result.next()

      result.getString(1) shouldEqual "PLMZ"
      result.getString(2) shouldEqual "Mazowieckie"
      result.getLong(3) shouldEqual 1000
      result.getObject(4) should beNull
    }
  }

  "Territory saving test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val t1 = new Territory("PLSL", "Śląskie", 2000, None, false, false)

      //when
      territoryDao.insert(t1)

      //then
      val query = session.prepareStatement("SELECT code, name, population, container FROM territories WHERE territories.code = ?")
      query.setString(1, "PLSL")
      val result = query.executeQuery()
      result.next()

      result.getString(1) shouldEqual "PLSL"
      result.getString(2) shouldEqual "Śląskie"
      result.getLong(3) shouldEqual 2000
      result.getObject(4) should beNull
    }
  }

  "Territory deletion test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val territory = territoryDao.find("PLPK").get

      //when
      territoryDao.delete(territory)

      //then
      val query = session.prepareStatement("SELECT code, name, population, container FROM territories WHERE territories.code = ?")
      query.setString(1, "PLPK")
      val result = query.executeQuery()

      result.next() should beFalse
    }
  }

  "Get children territories test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val territories = territoryDao.getChildrenTerritories(ter3.id)

      //then
      territories should have size 2
      territories should containAllOf(ter1 :: ter2 :: Nil)
    }
  }

  "Get children territories cascade test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val territories = territoryDao.getAllWithinTerritoryCascade(ter4.code)

      //then
      territories should have size 3
      territories should containAllOf(ter3 :: ter2 :: ter1 :: Nil)
    }
  }
}
