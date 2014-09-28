package db_access.table

import scala.slick.driver.PostgresDriver.simple._

case class TournamentsTable(tag: Tag) extends Table[(String)](tag, "tournaments") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)

  def * = name
}