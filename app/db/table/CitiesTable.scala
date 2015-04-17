package db.table

import db.row.CityDBRow
import models.territory.City
import play.api.db.slick.Config.driver.simple._
import scaldi.Injector

case class NewCityDBRow(name: String, population: Int, points: Int, territoryId: Long, latitude: Double, longitude: Double)

class CitiesTable(tag: Tag)(implicit inj: Injector) extends BaseTable[City, CityDBRow](tag, "cities") {
   def name = column[String]("name", O.NotNull)
   def population = column[Int]("population", O.NotNull)
   def points = column[Int]("points", O.NotNull)
   def territoryId = column[Long]("container", O.NotNull)
   def latitude = column[Double]("latitude", O.NotNull)
   def longitude = column[Double]("longitude", O.NotNull)

   def * = (id, name, population, points, territoryId, latitude, longitude) <> ((CityDBRow.apply _).tupled, CityDBRow.unapply)
   def autoInc = (name, population, points, territoryId, latitude, longitude) <> (NewCityDBRow.tupled, NewCityDBRow.unapply)
}
