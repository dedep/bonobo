package db.dao

import db.row.model.TerritoryRow
import db.table.TerritoriesTable
import models.Common._
import models.territory.Territory

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.Query

trait TerritoryDao {

  val selectQuery: Query[TerritoriesTable, TerritoryRow, Seq]

  def find(code: String)(implicit rs: JdbcBackend#Session): Option[Territory]

  def getChildrenTerritories(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Territory]

  def getAllWithinTerritoryCascade(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Territory]

  def find(ids: Seq[Id])(implicit rs: JdbcBackend#Session): Option[Territory]

  def findAll(ids: Seq[Id])(implicit rs: JdbcBackend#Session): List[Territory]

  def update(t: Territory, oldId: Id)(implicit rs: JdbcBackend#Session): Unit

  def delete(e: Territory)(implicit rs: JdbcBackend#Session): Unit

  def insert(a: Territory)(implicit rs: JdbcBackend#Session): Id
}
