package db.table

import db.row.model.CityRow
import models.territory.City
import play.api.db.slick.Config.driver.simple._
import scaldi.Injector

class CitiesTable(tag: Tag)(implicit inj: Injector) extends BaseTable[City, CityRow](tag, "cities") {
   def name = column[String]("name", O.NotNull)
   def population = column[Int]("population", O.NotNull)
   def points = column[Int]("points", O.NotNull)
   def territoryId = column[Long]("container", O.NotNull)
   def latitude = column[Double]("latitude", O.NotNull)
   def longitude = column[Double]("longitude", O.NotNull)

   def * = (id, name, population, points, territoryId, latitude, longitude) <> ((CityRow.apply _).tupled, CityRow.unapply)
}
