package models.table

import scala.slick.driver.PostgresDriver.simple._

class TerritoriesTable(tag: Tag) extends Table[(Long, String, Long, Option[Long])](tag, "territories") {
   def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
   def name = column[String]("name", O.NotNull)
   def population = column[Long]("population", O.NotNull)
   def containerId = column[Option[Long]]("container",O.Nullable)

   def * = (id, name, population, containerId)
 }
