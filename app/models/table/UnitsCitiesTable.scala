package models.table

import play.api.db.slick.Config.driver.simple._

class UnitsCitiesTable(tag: Tag) extends Table[(Long, Long, Double, Int, Int)](tag, "units_cities") {
  def cityId = column[Long]("city_id", O.NotNull)
  def unitId = column[Long]("unit_id", O.NotNull)
  def points = column[Double]("points", O.NotNull)
  def goalsScored = column[Int]("goals_scored", O.NotNull)
  def goalsConceded = column[Int]("goals_conceded", O.NotNull)

  def * = (cityId, unitId, points, goalsScored, goalsConceded)

  def pk = primaryKey("pk_a", (cityId, unitId))
}
