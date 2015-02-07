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
  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  private val ds = TableQuery[TerritoriesTable]

  override val selectQuery = for (territory <- ds) yield territory

  override def find(code: String)(implicit rs: JdbcBackend#Session): Option[Territory] =
    fromFilter(_.code === code)

  override def fromRow(row: TerritoryDBRow)(implicit rs: JdbcBackend#Session) =
    new Territory(row.code, row.name, row.population, row.containerCode.flatMap(find), row.isCountry, row.modifiable)

  override def findAll(implicit rs: JdbcBackend#Session): List[Territory] =
    selectQuery.list.map(fromRow)

  override def save(t: Territory)(implicit rs: JdbcBackend#Session): String =
    ds returning ds.map(_.code) +=
      TerritoryDBRow(t.code, t.name, t.population, t.container.map(_.code), t.isCountry, t.modifiable)
      .log(x => "Saving new territory " + x.code).info()

  override def update(t: Territory)(implicit rs: JdbcBackend#Session): Unit =
    update(t, t.code)

  override def update(t: Territory, oldCode: String)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.code === oldCode)
      .update(TerritoryDBRow(t.code, t.name, t.population, t.container.map(_.code), t.isCountry, t.modifiable))(rs)
      .log(x => "Updating territory " + t.code).info()

  override def delete(t: Territory)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.code === t.code)
      .delete
      .log(x => "Deleting territory " + t.code).info()

  override def getChildrenTerritories(territoryCode: String)(implicit rs: JdbcBackend#Session): List[Territory] =
    selectQuery.filter(_.containerCode === territoryCode).list.map(fromRow)

  override def getChildrenTerritories(t: Territory)(implicit rs: JdbcBackend#Session): List[Territory] =
    getChildrenTerritories(t.code)

  override def getAllWithinTerritoryCascade(territoryCode: String)(implicit rs: JdbcBackend#Session): List[Territory] = {
    def iterate(territoryCode: String, acc: List[Territory]): List[Territory] = {
      getChildrenTerritories(territoryCode) match {
        case Nil => acc
        case tr  => acc ::: tr.flatMap(t => iterate(t.code, List(t)))
      }
    }

    iterate(territoryCode, Nil)
  }

  private def fromFilter(filter: TerritoriesTable => Column[Boolean])(implicit rs: JdbcBackend#Session): Option[Territory] =
    selectQuery.filter(filter)
      .firstOption
      .log("Getting territory " + _.map(_.name)).info()
      .map(fromRow)
}