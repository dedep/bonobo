package db_access.dao.territory

import com.typesafe.scalalogging.slf4j.Logger
import db_access.dao.city.CityDao
import db_access.table.TerritoriesTable
import models.territory.{City, Territory}
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

class TerritoryDaoImpl(implicit inj: Injector) extends TerritoryDao with Injectable {
  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  private val ds = TableQuery[TerritoriesTable]

  private def fromFilter(filter: TerritoriesTable => Column[Boolean])(implicit rs: JdbcBackend#Session): Option[Territory] = {
    (for (territory <- ds if filter(territory)) yield territory).firstOption match {
      case None => None
      case Some((id: Long, name: String, population: Long, containerId: Option[Long], code: String)) =>
        containerId match {
          case None => Some(new Territory(id, name, population, None, code))
          case Some(cid: Long) => Some(new Territory(id, name, population, fromId(cid), code))
        }
    }
  }

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[Territory] = fromFilter(_.id === id)

  override def fromCode(code: String)(implicit rs: JdbcBackend#Session): Option[Territory] = fromFilter(_.code === code)

  override def update(t: Territory)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Updating territory: " + t.name)

    ds.filter(_.id === t.id).update(t.id, t.name, t.population, t.container.map(_.id), t.code)
  }

  override def getChildrenTerritories(t: Territory)(implicit rs: JdbcBackend#Session): List[Territory] =
    (for (territory <- ds if territory.containerId === t.id) yield territory).list.map(a => fromId(a._1).get)
}