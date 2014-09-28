package db_access.dao.city

import models.territory.{Territory, City}
import scala.slick.jdbc.JdbcBackend

trait CityDao {

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[City]

  def saveOrUpdate(c: City)(implicit rs: JdbcBackend#Session): Long

  def getAllWithinTerritoryCascade(t: Territory)(implicit rs: JdbcBackend#Session): List[City]

  def getAllWithinTerritory(t: Territory)(implicit rs: JdbcBackend#Session): List[City]
}
