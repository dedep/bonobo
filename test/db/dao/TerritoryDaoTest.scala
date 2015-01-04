package db.dao

import db.dao.city.CityDao
import db.dao.territory.{TerritoryDaoImpl, TerritoryDao}
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

  "Territory.fromId returns proper Territory" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val territory = territoryDao.fromId(2)

      //then
      territory should beSome[Territory]
      territory.get.id shouldEqual 2
      territory.get.name shouldEqual "Podkarpackie"
      territory.get.population shouldEqual 2129951

      territory.get.container should beSome[Territory]
      territory.get.container.get.id shouldEqual 3
      territory.get.container.get.name shouldEqual "Poland"
      territory.get.container.get.population shouldEqual 1
    }
  }

  "Get children territories test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase

      //when
      val territories = territoryDao.getChildrenTerritories(ter3)

      //then
      territories should have size 2
      territories.map(_.name) should containAllOf("Podkarpackie" :: "Zachodniopomorskie" :: Nil)
    }
  }

  "Territory updating test" in new WithApplication {
    play.api.db.slick.DB("test").withSession { implicit session =>
      //given
      TestUtils.insertTestTournamentIntoDatabase
      val t1 = new Territory(1, "Mazowieckie", 1000, None, "", false, false)

      //when
      territoryDao.update(t1, "PLMX")

      //then
      val query = session.prepareStatement("SELECT id, name, population, container FROM territories WHERE territories.id = ?")
      query.setLong(1, 1)
      val result = query.executeQuery()
      result.next()

      result.getLong(1) shouldEqual 1
      result.getString(2) shouldEqual "Mazowieckie"
      result.getLong(3) shouldEqual 1000
      result.getObject(4) should beNull
    }
  }
}
