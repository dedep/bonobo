package db.dao

import db.row.model.TerritoryRow
import db.table.TerritoriesTable
import models.territory.Territory

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.Query

trait TerritoryDao extends BaseCrudDao[Territory] {

  val selectQuery: Query[TerritoriesTable, TerritoryRow, Seq]

  def find(code: String)(implicit rs: JdbcBackend#Session): Option[Territory]

  def getChildrenTerritories(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Territory]

  def getAllWithinTerritoryCascade(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Territory]

  override protected type RowType = TerritoryRow
  override protected type TableType = TerritoriesTable
}
