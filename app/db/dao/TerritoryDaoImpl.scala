package db.dao

import com.typesafe.scalalogging.slf4j.Logger
import db.row.mapper.TerritoryRowMapper
import db.table.TerritoriesTable
import models.Common._
import models.territory.Territory
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}
import utils.FunLogger._

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

class TerritoryDaoImpl(implicit inj: Injector) extends TerritoryDao with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  protected val ds = TableQuery[TerritoriesTable]
  protected val dbRowService = inject[TerritoryRowMapper]

  override val selectQuery = for (territory <- ds) yield territory

  override def getChildrenTerritories(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Territory] =
    selectQuery.filter(_.container === territoryId).list.map(_.toEntity)

  override def getAllWithinTerritoryCascade(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Territory] = {
    def iterate(territoryCode: Long, acc: List[Territory]): List[Territory] = {
      getChildrenTerritories(territoryId) match {
        case Nil => acc
        case tr  => acc ::: tr.flatMap(t => iterate(t.id.get, List(t)))
      }
    }

    iterate(territoryId, Nil)
  }

  override def find(code: String)(implicit rs: JdbcBackend#Session): Option[Territory] =
    fromFilter(_.code === code).headOption

  def delete(e: Territory)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.id === e.id)
      .delete
      .plainLog(s"Deleting entity ${e.getClass} with id = ${e.id}.").info()

  def insert(a: Territory)(implicit rs: JdbcBackend#Session): Id =
    ((ds returning ds.map(_.id)) += dbRowService.fromEntity(a))
      .log(id => s"Adding entity ${a.getClass} with id = $id").info()

  def insertAll(a: Seq[Territory])(implicit rs: JdbcBackend#Session): Seq[Id] =
    ((ds returning ds.map(_.id)) ++= a.map { e =>
      dbRowService.fromEntity(e)
    })
      .log(id => s"Adding entities with ids = $id").info()

  def update(t: Territory, oldId: Id)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.id === oldId)
      .update(dbRowService.fromEntity(t))(rs)
      .plainLog(s"Updating entity ${t.getClass} with id = $oldId").info()

  def find(ids: Seq[Id])(implicit rs: JdbcBackend#Session): Option[Territory] =
    fromFilter(_.id === ids.last).headOption

  def findAll(ids: Seq[Id])(implicit rs: JdbcBackend#Session): List[Territory] =
    ds.list.map(_.toEntity)

  protected def fromFilter(filter: TerritoriesTable => Column[Boolean])(implicit rs: JdbcBackend#Session): List[Territory] =
    ds.filter(filter).list.map(_.toEntity)
}