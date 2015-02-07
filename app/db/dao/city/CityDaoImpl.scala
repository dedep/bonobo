package db.dao.city

import com.typesafe.scalalogging.slf4j.Logger
import db.dao.territory.TerritoryDao
import db.table.{NewCityDBRow, CitiesTable, CityDBRow, TerritoryDBRow}
import models.territory.City
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}
import utils.FunLogger._

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

class CityDaoImpl(implicit inj: Injector) extends CityDao with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  private val territoryDao = inject[TerritoryDao]

  private val ds = TableQuery[CitiesTable]

  val selectQuery =
    for {
      city <- ds
      terr <- territoryDao.selectQuery
      if terr.code === city.territoryCode
    } yield (city, terr)

  override def find(id: Long)(implicit rs: JdbcBackend#Session): Option[City] = fromId(Seq(id)).headOption

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

  override def fromRow(row: (CityDBRow, TerritoryDBRow))(implicit rs: JdbcBackend#Session): City = row match {
    case (citiesRow: CityDBRow, territoriesRow: TerritoryDBRow) =>
      val t = territoryDao.fromRow(territoriesRow)
      new City(citiesRow.id, citiesRow.name, citiesRow.population, citiesRow.points, t, citiesRow.latitude, citiesRow.longitude)
  }

  override def update(c: City, id: Long)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.id === c.id)
      .update(CityDBRow(c.id, c.name, c.population, c.points, c.territory.code, c.latitude, c.longitude))
      .plainLog("Updating city in DB " + c).info()

  override def save(c: City)(implicit rs: JdbcBackend#Session): Long =
    ds.map(_.autoInc) returning ds.map(_.id) +=
      NewCityDBRow(c.name, c.population, c.points, c.territory.code, c.latitude, c.longitude)
      .plainLog("Saving new city in DB " + c).info()

  override def delete(c: City)(implicit rs: JdbcBackend#Session) =
    ds.filter(_.id === c.id).delete
      .plainLog("Deleting city: " + c.name + " in database.")

  override def getAllWithinTerritoryCascade(territoryCode: String)(implicit rs: JdbcBackend#Session): List[City] =
    territoryDao.getChildrenTerritories(territoryCode) match {
      case Nil => getAllWithinTerritory(territoryCode)
      case tr => getAllWithinTerritory(territoryCode) ::: tr.flatMap(c => getAllWithinTerritoryCascade(c.code))
    }

  override def getAllWithinTerritory(territoryCode: String)(implicit rs: JdbcBackend#Session): List[City] =
    ds.filter(_.territoryCode === territoryCode).map(_.id).list.map(find(_).get)
}