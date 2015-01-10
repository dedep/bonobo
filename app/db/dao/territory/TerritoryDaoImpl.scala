package db.dao.territory

import com.typesafe.scalalogging.slf4j.Logger
import db.table.{NewTerritoryDBRow, TerritoriesTable, TerritoryDBRow}
import models.territory.Territory
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}
import utils.FunLogger._

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

class TerritoryDaoImpl(implicit inj: Injector) extends TerritoryDao with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  private val ds = TableQuery[TerritoriesTable]

  override val selectQuery = for (territory <- ds) yield territory

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[Territory] = 
    fromFilter(_.id === id)

  override def fromCode(code: String)(implicit rs: JdbcBackend#Session): Option[Territory] = 
    fromFilter(_.code === code)

  override def fromRow(row: TerritoryDBRow)(implicit rs: JdbcBackend#Session) =
    new Territory(row.id, row.name, row.population, row.containerId.flatMap(fromId), row.code, row.isCountry, row.modifiable)

  override def findAll()(implicit rs: JdbcBackend#Session): List[Territory] =
    selectQuery.list.map(fromRow)

  override def save(t: Territory)(implicit rs: JdbcBackend#Session): Long =
    ds.map(_.autoInc) +=
      NewTerritoryDBRow(t.name, t.population, t.container.map(_.id), t.code, t.isCountry, t.modifiable)
      .log(x => "Saving new territory " + x.code).info()

  override def update(t: Territory, oldCode: String)(rs: JdbcBackend#Session): Long =
    ds.filter(_.code === oldCode)
      .update(TerritoryDBRow(t.id, t.name, t.population, t.container.map(_.id), t.code, t.isCountry, t.modifiable))(rs)
      .log(x => "Updating territory " + t.code).info()

  override def delete(t: Territory)(rs: JdbcBackend#Session): Long =
    ds.filter(_.code === t.code)
      .delete(rs)
      .log(x => "Deleting territory " + t.code).info()

  override def getChildrenTerritories(t: Territory)(implicit rs: JdbcBackend#Session): List[Territory] =
    selectQuery.filter(_.containerId === t.id).list.map(fromRow)

  private def fromFilter(filter: TerritoriesTable => Column[Boolean])(implicit rs: JdbcBackend#Session): Option[Territory] =
    selectQuery.filter(filter)
      .firstOption
      .log("Getting territory " + _.map(_.name)).info()
      .map(fromRow)
}