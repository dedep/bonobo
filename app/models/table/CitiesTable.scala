package models.table

import scala.slick.driver.PostgresDriver.simple._

class CitiesTable(tag: Tag) extends Table[(Long, String, Int, Int, Long)](tag, "cities") {
   def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
   def name = column[String]("name", O.NotNull)
   def population = column[Int]("population", O.NotNull)
   def points = column[Int]("points",O.NotNull)
   def territoryId = column[Long]("container", O.NotNull)

   def * = (id, name, population, points, territoryId)
 }
