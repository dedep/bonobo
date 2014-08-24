package models.table

import play.api.db.slick.Config.driver.simple._

class RoundsCitiesTable(tag: Tag) extends Table[(Long, Long, Option[Int])](tag, "rounds_cities") {
  def roundId = column[Long]("round_id", O.NotNull)
  def cityId = column[Long]("city_id", O.NotNull)
  def pot = column[Option[Int]]("pot", O.Nullable)

  def * = (roundId, cityId, pot)

  def pk = primaryKey("pk_a", (roundId, cityId))
}