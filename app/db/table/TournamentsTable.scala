package db.table

import scala.slick.driver.PostgresDriver.simple._

class TournamentsTable(tag: Tag) extends Table[(String, String, Long)](tag, "tournaments") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def status = column[String]("status", O.NotNull)
  def territoryId = column[Long]("territory_id", O.NotNull)

  def * = (name, status, territoryId)
}