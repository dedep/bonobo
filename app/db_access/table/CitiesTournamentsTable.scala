package db_access.table

import play.api.db.slick.Config.driver.simple._

class CitiesTournamentsTable(tag: Tag) extends Table[(Long, Long, Boolean)](tag, "cities_tournaments") {
  def cityId = column[Long]("city_id", O.NotNull)
  def tournamentId = column[Long]("tournament_id", O.NotNull)
  def isTeamStillPlaying = column[Boolean]("is_playing", O.NotNull)

  def * = (cityId, tournamentId, isTeamStillPlaying)

  def pk = primaryKey("pk_a", (tournamentId, cityId))
}
