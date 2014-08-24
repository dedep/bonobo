package models.table

import play.api.db.slick.Config.driver.simple._

class CitiesTournamentsTable(tag: Tag) extends Table[(Long, Long)](tag, "cities_tournaments") {
  def cityId = column[Long]("city_id", O.NotNull)
  def tournamentId = column[Long]("tournament_id", O.NotNull)

  def * = (cityId, tournamentId)

  def pk = primaryKey("pk_a", (tournamentId, cityId))
}
