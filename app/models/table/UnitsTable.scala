package models.table

import play.api.db.slick.Config.driver.simple._

class UnitsTable(tag: Tag) extends Table[(Long, Long, String)](tag, "units") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def roundId = column[Long]("round_id", O.NotNull)
  def clazz = column[String]("class", O.NotNull)

  def * = (id, roundId, clazz)
}