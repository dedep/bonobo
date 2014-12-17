package db_access.table

import play.api.db.slick.Config.driver.simple._

class UnitsCitiesTable(tag: Tag) extends Table[(Long, Long, Double, Int, Int, Int, Int, Int, Option[Boolean])](tag, "units_cities") {
  def cityId = column[Long]("city_id", O.NotNull)
  def unitId = column[Long]("unit_id", O.NotNull)
  def points = column[Double]("points", O.NotNull)
  def goalsScored = column[Int]("goals_scored", O.NotNull)
  def goalsConceded = column[Int]("goals_conceded", O.NotNull)
  def wins = column[Int]("wins", O.NotNull)
  def draws = column[Int]("draws", O.NotNull)
  def loses = column[Int]("loses", O.NotNull)
  def promoted = column[Option[Boolean]]("promoted", O.Nullable)

  def * = (cityId, unitId, points, goalsScored, goalsConceded, wins, draws, loses, promoted)

  def pk = primaryKey("pk_a", (cityId, unitId))
}
