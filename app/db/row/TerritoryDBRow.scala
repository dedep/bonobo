package db.row

import db.dao.territory.TerritoryDao
import models.territory.Territory
import scaldi.{Injector, Injectable}

import scala.slick.jdbc.JdbcBackend

case class TerritoryDBRow(override val id: Long, code: String, name: String, population: Long, container: Option[Long],
                          isCountry: Boolean, modifiable: Boolean)
                         (implicit inj: Injector)
  extends BaseDBRow[Territory](id) with Injectable {

  val territoryDao = inject[TerritoryDao]

  override def toEntity(implicit rs: JdbcBackend#Session): Territory = new Territory(id, code, name,
    population, container.flatMap(c => territoryDao.find(c :: Nil)), isCountry, modifiable)
}

class TerritoryDBRowService(implicit inj: Injector) extends BaseDBRowService[Territory] {
  override def fromEntity(t: Territory): TerritoryDBRow =
    TerritoryDBRow(t.id, t.code, t.name, t.population, t.container.map(_.id), t.isCountry, t.modifiable)
}