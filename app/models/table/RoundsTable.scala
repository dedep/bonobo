package models.table

import play.api.db.slick.Config.driver.simple._

class RoundsTable(tag: Tag) extends Table[(String, String, Int, Boolean, Long)](tag, "rounds") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def clazz = column[String]("class", O.NotNull)
  def step = column[Int]("step", O.NotNull)
  def isPreliminary = column[Boolean]("is_preliminary", O.NotNull)
  def tournamentId = column[Long]("tournament_id", O.NotNull)
  def order = column[Long]("round_order", O.NotNull, O.AutoInc)

  def * = (name, clazz, step, isPreliminary, tournamentId)
}