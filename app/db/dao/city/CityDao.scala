package db.dao.city

import db.dao.BaseCrudDao
import db.row.{TerritoryDBRow, CityDBRow}
import db.table.{CitiesTable, TerritoriesTable}
import models.territory.City
import models.Common._

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.Query

trait CityDao extends BaseCrudDao[City] {

  val selectQuery: Query[(CitiesTable, TerritoriesTable), (CityDBRow, TerritoryDBRow), Seq]

  override def findAll(ids: Seq[Id])(implicit rs: JdbcBackend#Session): List[City] =
    getAllWithinTerritoryCascade(ids.last)

  def fromId(ids: Seq[Id])(implicit rs: JdbcBackend#Session): List[City]

  def getAllWithinTerritoryCascade(territoryId: Id)(implicit rs: JdbcBackend#Session): List[City]

  def getAllWithinTerritory(territoryId: Id)(implicit rs: JdbcBackend#Session): List[City]

  def fromRow(row: (CityDBRow, TerritoryDBRow))(implicit rs: JdbcBackend#Session): City

  override protected type RowType = CityDBRow
  override protected type TableType = CitiesTable
}
