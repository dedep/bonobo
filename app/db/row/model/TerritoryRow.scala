package db.row.model

import db.dao.TerritoryDao
import models.territory.Territory
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend

case class TerritoryRow(override val id: Long, code: String, name: String, population: Long, container: Option[Long],
                          isCountry: Boolean, modifiable: Boolean)
                         (implicit inj: Injector)
  extends BaseRow[Territory](id) with Injectable {

  val territoryDao = inject[TerritoryDao]

  override def toEntity(implicit rs: JdbcBackend#Session): Territory = new Territory(Some(id), code, name,
    population, container.flatMap(c => territoryDao.find(c :: Nil)), isCountry, modifiable)
}
