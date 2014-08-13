package models.table

import scala.slick.driver.PostgresDriver.simple._

class CitiesTournamentsTable(tag: Tag) extends Table[(Long, Long, Long)](tag, "cities_tournaments") {
   def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
   def cityId = column[Long]("city_id", O.NotNull)
   def tournamentId = column[Long]("tournament_id", O.NotNull)

   def * = (id, cityId, tournamentId)
 }
