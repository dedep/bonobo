package db.dao.city

import db.table.{CitiesTable, CityDBRow, TerritoriesTable, TerritoryDBRow}
import models.territory.{City, Territory}

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.Query

trait CityDao {

  val selectQuery: Query[(CitiesTable, TerritoriesTable), (CityDBRow, TerritoryDBRow), Seq]

  def fromId(ids: Seq[Long])(implicit rs: JdbcBackend#Session): List[City]

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[City]

  def fromRow(row: (CityDBRow, TerritoryDBRow), id: Long)(implicit rs: JdbcBackend#Session): City

  def saveOrUpdate(c: City)(implicit rs: JdbcBackend#Session): Long

  def getAllWithinTerritoryCascade(t: Territory)(implicit rs: JdbcBackend#Session): List[City]

  def getAllWithinTerritory(t: Territory)(implicit rs: JdbcBackend#Session): List[City]
}