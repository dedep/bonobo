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

  override def find(id: Long)(implicit rs: JdbcBackend#Session): Option[Territory] =
    fromFilter(_.id === id)

  override def fromRow(row: TerritoryDBRow)(implicit rs: JdbcBackend#Session) =
    new Territory(row.id, row.code, row.name, row.population, row.container.flatMap(find), row.isCountry, row.modifiable)

  override def findAll(implicit rs: JdbcBackend#Session): List[Territory] =
    selectQuery.list.map(fromRow)

  override def save(t: Territory)(implicit rs: JdbcBackend#Session): Long =
    ds.map(_.autoInc) returning ds.map(_.id) +=
      NewTerritoryDBRow(t.code, t.name, t.population, t.container.map(_.id), t.isCountry, t.modifiable)
      .log(x => "Saving new territory " + x.code).info()

  override def update(t: Territory)(implicit rs: JdbcBackend#Session): Unit =
    update(t, t.id)

  override def update(t: Territory, oldId: Long)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.id === oldId)
      .update(TerritoryDBRow(t.id, t.code, t.name, t.population, t.container.map(_.id), t.isCountry, t.modifiable))(rs)
      .log(x => "Updating territory " + t.code).info()

  override def delete(t: Territory)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.code === t.code)
      .delete
      .log(x => "Deleting territory " + t.code).info()

  override def getChildrenTerritories(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Territory] =
    selectQuery.filter(_.container === territoryId).list.map(fromRow)

  override def getChildrenTerritories(t: Territory)(implicit rs: JdbcBackend#Session): List[Territory] =
    getChildrenTerritories(t.id)

  override def getAllWithinTerritoryCascade(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Territory] = {
    def iterate(territoryCode: Long, acc: List[Territory]): List[Territory] = {
      getChildrenTerritories(territoryId) match {
        case Nil => acc
        case tr  => acc ::: tr.flatMap(t => iterate(t.id, List(t)))
      }
    }

    iterate(territoryId, Nil)
  }

  private def fromFilter(filter: TerritoriesTable => Column[Boolean])(implicit rs: JdbcBackend#Session): Option[Territory] =
    selectQuery.filter(filter)
      .firstOption
      .log("Getting territory " + _.map(_.name)).info()
      .map(fromRow)

  override def find(code: String)(implicit rs: JdbcBackend#Session): Option[Territory] = fromFilter(_.code === code)
}