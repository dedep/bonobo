package models.table

import play.api.db.slick.Config.driver.simple._

//todo: klucz jako kombinacja kolumn
class UnitsCitiesTable(tag: Tag) extends Table[(Long, Long, Long, Int, Int, Int)](tag, "units_cities") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def cityId = column[Long]("city_id", O.NotNull)
  def unitId = column[Long]("unit_id", O.NotNull)
  def points = column[Int]("points", O.NotNull)
  def goalsScored = column[Int]("goals_scored", O.NotNull)
  def goalsConceded = column[Int]("goals_conceded", O.NotNull)

  def * = (id, cityId, unitId, points, goalsScored, goalsConceded)
}