package db.dao.city

import db.dao.BaseCrudDao
import db.table.{CitiesTable, CityDBRow, TerritoriesTable, TerritoryDBRow}
import models.territory.City

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.Query

trait CityDao extends BaseCrudDao[City] {

  val selectQuery: Query[(CitiesTable, TerritoriesTable), (CityDBRow, TerritoryDBRow), Seq]

  def fromId(ids: Seq[Long])(implicit rs: JdbcBackend#Session): List[City]

  override def find(id: Long)(implicit rs: JdbcBackend#Session): Option[City]

  def fromRow(row: (CityDBRow, TerritoryDBRow))(implicit rs: JdbcBackend#Session): City

  override def save(c: City)(implicit rs: JdbcBackend#Session): Long

  override def delete(c: City)(implicit rs: JdbcBackend#Session): Unit

  override def update(t: City, oldCode: Long)(implicit rs: JdbcBackend#Session): Unit

  override def findAll(implicit rs: JdbcBackend#Session): List[City] = ???

  def getAllWithinTerritoryCascade(territoryCode: Long)(implicit rs: JdbcBackend#Session): List[City]

  def getAllWithinTerritory(territoryCode: Long)(implicit rs: JdbcBackend#Session): List[City]
}
