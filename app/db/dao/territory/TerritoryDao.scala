package db.dao.territory

import db.dao.BaseCrudDao
import db.table.{TerritoriesTable, TerritoryDBRow}
import models.territory.Territory

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.Query

trait TerritoryDao extends BaseCrudDao[Territory, String] {

  val selectQuery: Query[TerritoriesTable, TerritoryDBRow, Seq]

  override def find(code: String)(implicit rs: JdbcBackend#Session): Option[Territory]

  override def findAll(implicit rs: JdbcBackend#Session): List[Territory]

  def fromRow(row: TerritoryDBRow)(implicit rs: JdbcBackend#Session): Territory

  override def save(t: Territory)(implicit rs: JdbcBackend#Session): String

  def update(t: Territory)(implicit rs: JdbcBackend#Session): Unit

  override def update(t: Territory, oldCode: String)(implicit rs: JdbcBackend#Session): Unit

  override def delete(t: Territory)(implicit rs: JdbcBackend#Session): Unit

  def getChildrenTerritories(t: Territory)(implicit rs: JdbcBackend#Session): List[Territory]

  def getChildrenTerritories(territoryCode: String)(implicit rs: JdbcBackend#Session): List[Territory]

  def getAllWithinTerritoryCascade(territoryCode: String)(implicit rs: JdbcBackend#Session): List[Territory]
}
