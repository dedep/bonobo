package db.dao

import db.row.model.{TerritoryRow, CityRow}
import db.table.{CitiesTable, TerritoriesTable}
import models.Common._
import models.territory.City

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.Query

trait CityDao extends BaseCrudDao[City] {

  val selectQuery: Query[(CitiesTable, TerritoriesTable), (CityRow, TerritoryRow), Seq]

  override def findAll(ids: Seq[Id])(implicit rs: JdbcBackend#Session): List[City] =
    getAllWithinTerritoryCascade(ids.last)

  def fromId(ids: Seq[Id])(implicit rs: JdbcBackend#Session): List[City]

  def getAllWithinTerritoryCascade(territoryId: Id)(implicit rs: JdbcBackend#Session): List[City]

  def getAllWithinTerritory(territoryId: Id)(implicit rs: JdbcBackend#Session): List[City]

  def fromRow(row: (CityRow, TerritoryRow))(implicit rs: JdbcBackend#Session): City

  override protected type RowType = CityRow
  override protected type TableType = CitiesTable
}
