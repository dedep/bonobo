package models.table

import play.api.db.slick.Config.driver.simple._

class RoundsTable(tag: Tag) extends Table[(Long, String, Int, Boolean, Long)](tag, "rounds") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def clazz = column[String]("class", O.NotNull)
  def step = column[Int]("step", O.NotNull)
  def isPreliminary = column[Boolean]("is_preliminary", O.NotNull)
  def tournamentId = column[Long]("tournament_id", O.NotNull)

  def * = (id, clazz, step, isPreliminary, tournamentId)
}