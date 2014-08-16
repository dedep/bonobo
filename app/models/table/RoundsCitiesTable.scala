package models.table

import play.api.db.slick.Config.driver.simple._

//TODO: klucz jako kombinacja kolumn
class RoundsCitiesTable(tag: Tag) extends Table[(Long, Long, Long, Int)](tag, "rounds_cities") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def roundId = column[Long]("round_id", O.NotNull)
  def cityId = column[Long]("city_id", O.NotNull)
  def pot = column[Int]("pot", O.Nullable)

  def * = (id, roundId, cityId, pot)
}