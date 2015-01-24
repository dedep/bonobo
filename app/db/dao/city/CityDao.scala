package db.dao.city

import db.table.{CitiesTable, CityDBRow, TerritoriesTable, TerritoryDBRow}
import models.territory.City

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.Query

trait CityDao {

  val selectQuery: Query[(CitiesTable, TerritoriesTable), (CityDBRow, TerritoryDBRow), Seq]

  def fromId(ids: Seq[Long])(implicit rs: JdbcBackend#Session): List[City]

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[City]

  def fromRow(row: (CityDBRow, TerritoryDBRow))(implicit rs: JdbcBackend#Session): City

  def update(c: City)(implicit rs: JdbcBackend#Session): Long

  def save(c: City)(implicit rs: JdbcBackend#Session): Long

  def delete(c: City)(implicit rs: JdbcBackend#Session): Unit

  def getAllWithinTerritoryCascade(territoryCode: String)(implicit rs: JdbcBackend#Session): List[City]

  def getAllWithinTerritory(territoryCode: String)(implicit rs: JdbcBackend#Session): List[City]
}
