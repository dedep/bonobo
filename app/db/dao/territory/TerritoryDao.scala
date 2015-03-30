package db.dao.territory

import db.dao.BaseCrudDao
import db.table.{TerritoriesTable, TerritoryDBRow}
import models.territory.Territory

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.Query

trait TerritoryDao extends BaseCrudDao[Territory] {

  val selectQuery: Query[TerritoriesTable, TerritoryDBRow, Seq]

  override def find(code: Long)(implicit rs: JdbcBackend#Session): Option[Territory]

  override def findAll(implicit rs: JdbcBackend#Session): List[Territory]

  override def save(t: Territory)(implicit rs: JdbcBackend#Session): Long

  override def update(t: Territory, oldCode: Long)(implicit rs: JdbcBackend#Session): Unit

  override def delete(t: Territory)(implicit rs: JdbcBackend#Session): Unit

  def find(code: String)(implicit rs: JdbcBackend#Session): Option[Territory]

  def fromRow(row: TerritoryDBRow)(implicit rs: JdbcBackend#Session): Territory

  def update(t: Territory)(implicit rs: JdbcBackend#Session): Unit

  def getChildrenTerritories(t: Territory)(implicit rs: JdbcBackend#Session): List[Territory]

  def getChildrenTerritories(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Territory]

  def getAllWithinTerritoryCascade(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Territory]
}
