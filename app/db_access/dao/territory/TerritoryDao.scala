package db_access.dao.territory

import models.territory.Territory
import scala.slick.jdbc.JdbcBackend

trait TerritoryDao {

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[Territory]

  def fromCode(code: String)(implicit rs: JdbcBackend#Session): Option[Territory]

  def update(t: Territory)(implicit rs: JdbcBackend#Session): Long

  def getChildrenTerritories(t: Territory)(implicit rs: JdbcBackend#Session): List[Territory]
}
