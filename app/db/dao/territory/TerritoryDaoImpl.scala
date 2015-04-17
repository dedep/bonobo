package db.dao.territory

import com.typesafe.scalalogging.slf4j.Logger
import db.row.TerritoryDBRowService
import db.table.TerritoriesTable
import models.territory.Territory
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

class TerritoryDaoImpl(implicit inj: Injector) extends TerritoryDao with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  override protected val ds = TableQuery[TerritoriesTable]
  override protected val dbRowService = inject[TerritoryDBRowService]

  override val selectQuery = for (territory <- ds) yield territory

  override def getChildrenTerritories(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Territory] =
    selectQuery.filter(_.container === territoryId).list.map(_.toEntity)

  override def getAllWithinTerritoryCascade(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Territory] = {
    def iterate(territoryCode: Long, acc: List[Territory]): List[Territory] = {
      getChildrenTerritories(territoryId) match {
        case Nil => acc
        case tr  => acc ::: tr.flatMap(t => iterate(t.id, List(t)))
      }
    }

    iterate(territoryId, Nil)
  }

  override def find(code: String)(implicit rs: JdbcBackend#Session): Option[Territory] =
    fromFilter(_.code === code).headOption
}