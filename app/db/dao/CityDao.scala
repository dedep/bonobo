package db.dao

import db.row.model.{TerritoryRow, CityRow}
import db.table.{CitiesTable, TerritoriesTable}
import models.Common._
import models.territory.City

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.Query

trait CityDao {

  val selectQuery: Query[(CitiesTable, TerritoriesTable), (CityRow, TerritoryRow), Seq]

  def findAll(ids: Seq[Id])(implicit rs: JdbcBackend#Session): List[City]

  def fromId(ids: Seq[Id])(implicit rs: JdbcBackend#Session): List[City]

  def getAllWithinTerritoryCascade(territoryId: Id)(implicit rs: JdbcBackend#Session): List[City]

  def getAllWithinTerritory(territoryId: Id)(implicit rs: JdbcBackend#Session): List[City]

  def fromRow(row: (CityRow, TerritoryRow))(implicit rs: JdbcBackend#Session): City

  def update(t: City, oldId: Id)(implicit rs: JdbcBackend#Session): Unit

  def find(id: Seq[Id])(implicit rs: JdbcBackend#Session): Option[City]

  def delete(e: City)(implicit rs: JdbcBackend#Session): Unit

  def insert(a: City)(implicit rs: JdbcBackend#Session): Id
}
