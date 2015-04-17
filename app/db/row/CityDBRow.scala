package db.row

import db.dao.territory.TerritoryDao
import models.territory.City
import scaldi.{Injector, Injectable}

import scala.slick.jdbc.JdbcBackend

case class CityDBRow(override val id: Long, name: String, population: Int, points: Int, territoryId: Long, latitude: Double, longitude: Double)
                    (implicit inj: Injector)
   extends BaseDBRow[City](id) with Injectable {

   val territoryDao = inject[TerritoryDao]

   override def toEntity(implicit rs: JdbcBackend#Session): City =
      new City(id, name, population, points, territoryDao.find(territoryId :: Nil).get, latitude, longitude)
}

class CityDBRowService(implicit inj: Injector) extends BaseDBRowService[City] {
   override def fromEntity(c: City): BaseDBRow[City] =
      CityDBRow(c.id, c.name, c.population, c.points, c.territory.id, c.latitude, c.longitude)
}