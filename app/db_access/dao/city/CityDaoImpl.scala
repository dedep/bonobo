package db_access.dao.city

import com.typesafe.scalalogging.slf4j.Logger
import db_access.dao.territory.TerritoryDao
import db_access.table.{TerritoryDBRow, CityDBRow, CitiesTable}
import models.territory.{Territory, City}
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

import utils.FunLogger._

class CityDaoImpl(implicit inj: Injector) extends CityDao with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger(this.getClass))

  private val territoryDao = inject[TerritoryDao]

  private val ds = TableQuery[CitiesTable]

  val selectQuery =
    for {
      city <- ds
      terr <- territoryDao.selectQuery
      if terr.id === city.territoryId
    } yield (city, terr)

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[City] = fromId(Seq(id)).headOption

  override def fromId(ids: Seq[Long])(implicit rs: JdbcBackend#Session): List[City] =
    selectQuery
      .filter(_._1.id inSet ids)
      .map(q => (q._1.id, q._1, q._2))
      .log(x => "Query for cities " + x.selectStatement).info()
      .list
      .map { row =>
        val t = territoryDao.fromRow(row._3)
        val citiesResult = row._2
        val id = row._1

        new City(id, citiesResult.name, citiesResult.population, citiesResult.points, t, citiesResult.latitude, citiesResult.longitude)
    }

  //todo: ten id przeszkadza
  override def fromRow(row: (CityDBRow, TerritoryDBRow), id: Long)(implicit rs: JdbcBackend#Session): City = row match {
    case (citiesRow: CityDBRow, territoriesRow: TerritoryDBRow) =>
      val t = territoryDao.fromRow(territoriesRow)
      new City(id, citiesRow.name, citiesRow.population, citiesRow.points, t, citiesRow.latitude, citiesRow.longitude)
  }

  override def saveOrUpdate(c: City)(implicit rs: JdbcBackend#Session): Long = {
    if (territoryDao.fromId(c.territory.id).isEmpty)
      throw new IllegalStateException("City cannot refer to non-existent territory")

    if (fromId(c.id).isEmpty) save(c) else update(c)
  }

  private def save(c: City)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Saving new city in DB " + c)
    (ds returning ds.map(_.id)) += CityDBRow(c.name, c.population, c.points, c.territory.id, c.latitude, c.longitude)
  }

  private def update(c: City)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Updating city in DB " + c)
    ds.filter(_.id === c.id).update(CityDBRow(c.name, c.population, c.points, c.territory.id, c.latitude, c.longitude))
  }

  override def getAllWithinTerritoryCascade(t: Territory)(implicit rs: JdbcBackend#Session): List[City] =
    territoryDao.getChildrenTerritories(t) match {
      case Nil => getAllWithinTerritory(t)
      case el => getAllWithinTerritory(t) ::: el.flatMap(getAllWithinTerritoryCascade)
    }

  override def getAllWithinTerritory(t: Territory)(implicit rs: JdbcBackend#Session): List[City] =
    ds.filter(_.territoryId === t.id).map(_.id).list.map(fromId(_).get)
}