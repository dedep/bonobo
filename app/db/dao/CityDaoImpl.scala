package db.dao

import com.typesafe.scalalogging.slf4j.Logger
import db.row.mapper.CityRowMapper
import db.row.model.{TerritoryRow, CityRow}
import db.table._
import models.Common._
import models.territory.City
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}
import utils.FunLogger._

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

class CityDaoImpl(implicit inj: Injector) extends CityDao with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  protected val ds = TableQuery[CitiesTable]
  protected val dbRowService = inject[CityRowMapper]

  private val territoryDao = inject[TerritoryDao]

  override val selectQuery =
    for {
      city <- ds
      terr <- territoryDao.selectQuery
      if terr.id === city.territoryId
    } yield (city, terr)

  def find(id: Seq[Id])(implicit rs: JdbcBackend#Session): Option[City] = fromId(Seq(id.last)).headOption

  override def fromId(ids: Seq[Id])(implicit rs: JdbcBackend#Session): List[City] =
    selectQuery
      .filter(_._1.id inSet ids)
      .list
      .map(fromRow)

  override def fromRow(row: (CityRow, TerritoryRow))(implicit rs: JdbcBackend#Session): City = row match {
    case (citiesRow: CityRow, territoriesRow: TerritoryRow) =>
      new City(Some(citiesRow.id), citiesRow.name, citiesRow.population, citiesRow.points, territoriesRow.toEntity,
        citiesRow.latitude, citiesRow.longitude)
  }

  override def getAllWithinTerritoryCascade(territoryId: Long)(implicit rs: JdbcBackend#Session): List[City] =
    territoryDao.getChildrenTerritories(territoryId) match {
      case Nil => getAllWithinTerritory(territoryId)
      case tr => getAllWithinTerritory(territoryId) ::: tr.flatMap(t => getAllWithinTerritoryCascade(t.id.get))
    }

  override def getAllWithinTerritory(territoryId: Long)(implicit rs: JdbcBackend#Session): List[City] =
    selectQuery.filter(_._1.territoryId === territoryId).list.map(fromRow)

  def delete(e: City)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.id === e.id)
      .delete
      .plainLog(s"Deleting entity ${e.getClass} with id = ${e.id}.").info()

  def insert(a: City)(implicit rs: JdbcBackend#Session): Id =
    ((ds returning ds.map(_.id)) += dbRowService.fromEntity(a).asInstanceOf[CityRow])
      .log(id => s"Adding entity ${a.getClass} with id = $id").info()

  def insertAll(a: Seq[City])(implicit rs: JdbcBackend#Session): Seq[Id] =
    ((ds returning ds.map(_.id)) ++= a.map { e =>
      dbRowService.fromEntity(e).asInstanceOf[CityRow]})
      .log(id => s"Adding entities with ids = $id").info()

  def update(t: City, oldId: Id)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.id === oldId)
      .update(dbRowService.fromEntity(t).asInstanceOf[CityRow])(rs)
      .plainLog(s"Updating entity ${t.getClass} with id = $oldId").info()

  def findAll(ids: Seq[Id])(implicit rs: JdbcBackend#Session): List[City] =
    getAllWithinTerritoryCascade(ids.last)

  protected def fromFilter(filter: CitiesTable => Column[Boolean])(implicit rs: JdbcBackend#Session): List[City] =
    ds.filter(filter).list.map(_.toEntity)
}
