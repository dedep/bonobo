package db.dao.territory

import com.typesafe.scalalogging.slf4j.Logger
import db.table.{TerritoriesTable, TerritoryDBRow}
import models.territory.Territory
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}
import utils.FunLogger._

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

class TerritoryDaoImpl(implicit inj: Injector) extends TerritoryDao with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger(this.getClass))

  private val ds = TableQuery[TerritoriesTable]

  override val selectQuery = for (territory <- ds) yield territory

  private def fromFilter(filter: TerritoriesTable => Column[Boolean])(implicit rs: JdbcBackend#Session): Option[Territory] =
    selectQuery.filter(filter)
      .firstOption
      .log("Getting territory " + _.map(_.name)).info()
      .map(fromRow)

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[Territory] = fromFilter(_.id === id)

  override def fromCode(code: String)(implicit rs: JdbcBackend#Session): Option[Territory] = fromFilter(_.code === code)

  override def fromRow(row: TerritoryDBRow)(implicit rs: JdbcBackend#Session) =
    new Territory(row.id, row.name, row.population, row.containerId.flatMap(fromId), row.code)

  override def update(t: Territory)(implicit rs: JdbcBackend#Session): Long =
    ds.filter(_.id === t.id)
      .log(x => "Updating territory " + t).info()
      .update(TerritoryDBRow(t.id, t.name, t.population, t.container.map(_.id), t.code))

  override def getChildrenTerritories(t: Territory)(implicit rs: JdbcBackend#Session): List[Territory] =
  (for (territory <- ds if territory.containerId === t.id) yield territory).list.map(a => fromId(a.id).get)

}