package db_access.dao.city

import com.typesafe.scalalogging.slf4j.Logger
import db_access.dao.territory.TerritoryDao
import db_access.table.CitiesTable
import models.territory.{Territory, City}
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

class CityDaoImpl(implicit inj: Injector) extends CityDao with Injectable {
  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  private val territoryDao = inject[TerritoryDao]

  private val ds = TableQuery[CitiesTable]

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[City] =
    (for (city <- ds if city.id === id) yield city).firstOption match {
      case None => None
      case Some((name: String, population: Int, points: Int, container: Long, latitude: Double, longitude: Double)) =>
        Some(new City(id, name, population, points, territoryDao.fromId(container)
          .getOrElse(throw new IllegalStateException("City " + name + " references to non-existent territory")), latitude, longitude))
    }

  override def saveOrUpdate(c: City)(implicit rs: JdbcBackend#Session): Long = {
    if (territoryDao.fromId(c.territory.id).isEmpty)
      throw new IllegalStateException("City cannot refer to non-existent territory")

    if (fromId(c.id).isEmpty) save(c) else update(c)
  }

  private def save(c: City)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Saving new city in DB " + c)
    (ds returning ds.map(_.id)) += (c.name, c.population, c.points, c.territory.id, c.latitude, c.longitude)
  }

  private def update(c: City)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Updating city in DB " + c)
    ds.filter(_.id === c.id).update(c.name, c.population, c.points, c.territory.id, c.latitude, c.longitude)
  }

  override def getAllWithinTerritoryCascade(t: Territory)(implicit rs: JdbcBackend#Session): List[City] =
    territoryDao.getChildrenTerritories(t) match {
      case Nil => getAllWithinTerritory(t)
      case el => getAllWithinTerritory(t) ::: el.flatMap(getAllWithinTerritoryCascade)
    }

  override def getAllWithinTerritory(t: Territory)(implicit rs: JdbcBackend#Session): List[City] =
    ds.filter(_.territoryId === t.id).map(_.id).list.map(fromId(_).get)
}