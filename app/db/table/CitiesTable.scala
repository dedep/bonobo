package db.table

import play.api.db.slick.Config.driver.simple._

case class CityDBRow(id: Long, name: String, population: Int, points: Int, territoryId: Long, latitude: Double, longitude: Double)
case class NewCityDBRow(name: String, population: Int, points: Int, territoryId: Long, latitude: Double, longitude: Double)

class CitiesTable(tag: Tag) extends Table[CityDBRow](tag, "cities") {
   def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
   def name = column[String]("name", O.NotNull)
   def population = column[Int]("population", O.NotNull)
   def points = column[Int]("points", O.NotNull)
   def territoryId = column[Long]("container", O.NotNull)
   def latitude = column[Double]("latitude", O.NotNull)
   def longitude = column[Double]("longitude", O.NotNull)

   def * = (id, name, population, points, territoryId, latitude, longitude) <> (CityDBRow.tupled, CityDBRow.unapply)
   def autoInc = (name, population, points, territoryId, latitude, longitude) <> (NewCityDBRow.tupled, NewCityDBRow.unapply)
}
