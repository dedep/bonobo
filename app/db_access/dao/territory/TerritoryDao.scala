package db_access.dao.territory

import db_access.table.{TerritoryDBRow, TerritoriesTable}
import models.territory.Territory
import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.{Query, Column}

trait TerritoryDao {

  val selectQuery: /*(TerritoriesTable => Column[Boolean]) => */Query[TerritoriesTable, TerritoryDBRow, Seq]

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[Territory]

  def fromCode(code: String)(implicit rs: JdbcBackend#Session): Option[Territory]

  def fromRow(row: TerritoryDBRow)(implicit rs: JdbcBackend#Session): Territory

  def update(t: Territory)(implicit rs: JdbcBackend#Session): Long

  def getChildrenTerritories(t: Territory)(implicit rs: JdbcBackend#Session): List[Territory]
}
