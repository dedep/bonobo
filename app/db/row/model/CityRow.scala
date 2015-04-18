package db.row.model

import db.dao.TerritoryDao
import models.territory.City
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend

case class CityRow(override val id: Long, name: String, population: Int, points: Int, territoryId: Long, latitude: Double, longitude: Double)
                    (implicit inj: Injector)
   extends BaseRow[City](id) with Injectable {

   val territoryDao = inject[TerritoryDao]

   override def toEntity(implicit rs: JdbcBackend#Session): City =
      new City(Some(id), name, population, points, territoryDao.find(territoryId :: Nil).get, latitude, longitude)
}
