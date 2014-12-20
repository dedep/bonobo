package db.dao.territory

import db.table.{TerritoriesTable, TerritoryDBRow}
import models.territory.Territory

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.Query

trait TerritoryDao {

  val selectQuery: Query[TerritoriesTable, TerritoryDBRow, Seq]

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[Territory]

  def fromCode(code: String)(implicit rs: JdbcBackend#Session): Option[Territory]

  def fromRow(row: TerritoryDBRow)(implicit rs: JdbcBackend#Session): Territory

  def update(t: Territory)(implicit rs: JdbcBackend#Session): Long

  def getChildrenTerritories(t: Territory)(implicit rs: JdbcBackend#Session): List[Territory]
}
