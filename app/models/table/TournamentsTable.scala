package models.table

import scala.slick.driver.PostgresDriver.simple._

case class TournamentsTable(tag: Tag) extends Table[(Long)](tag, "tournaments") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def * = id
}