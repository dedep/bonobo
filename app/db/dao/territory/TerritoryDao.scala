package db.dao.territory

import db.table.{TerritoriesTable, TerritoryDBRow}
import models.territory.Territory

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.Query

trait TerritoryDao {

  val selectQuery: Query[TerritoriesTable, TerritoryDBRow, Seq]

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[Territory]

  def fromCode(code: String)(implicit rs: JdbcBackend#Session): Option[Territory]

  def findAll()(implicit rs: JdbcBackend#Session): List[Territory]

  def fromRow(row: TerritoryDBRow)(implicit rs: JdbcBackend#Session): Territory

  def save(t: Territory)(implicit rs: JdbcBackend#Session): Long

  def update(t: Territory, oldCode: String)(implicit rs: JdbcBackend#Session): Long

  def getChildrenTerritories(t: Territory)(implicit rs: JdbcBackend#Session): List[Territory]
}
