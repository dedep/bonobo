package db.table

import play.api.db.slick.Config.driver.simple._

class UnitsTable(tag: Tag) extends Table[(Long, String, String)](tag, "units") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def roundId = column[Long]("round_id", O.NotNull)
  def clazz = column[String]("class", O.NotNull)
  def name = column[String]("unit_name", O.NotNull)

  def * = (roundId, clazz, name)
}