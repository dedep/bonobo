package models.table

import play.api.db.slick.Config.driver.simple._

class CitiesTable(tag: Tag) extends Table[(String, Int, Int, Long, Double, Double)](tag, "cities") {
   def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
   def name = column[String]("name", O.NotNull)
   def population = column[Int]("population", O.NotNull)
   def points = column[Int]("points", O.NotNull)
   def territoryId = column[Long]("container", O.NotNull)
   def latitude = column[Double]("latitude", O.NotNull)
   def longitude = column[Double]("longitude", O.NotNull)

   def * = (name, population, points, territoryId, latitude, longitude)
}
