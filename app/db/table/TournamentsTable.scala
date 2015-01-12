package db.table

import scala.slick.driver.PostgresDriver.simple._

case class TournamentDBRow(id: Long, name: String, status: String, territoryId: Long)
case class NewTournamentDBRow(name: String, status: String, territoryId: Long)

class TournamentsTable(tag: Tag) extends Table[TournamentDBRow](tag, "tournaments") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def status = column[String]("status", O.NotNull)
  def territoryId = column[Long]("territory_id", O.NotNull)

  def * = (id, name, status, territoryId) <> (TournamentDBRow.tupled, TournamentDBRow.unapply)
  def autoInc = (name, status, territoryId) <> (NewTournamentDBRow.tupled, NewTournamentDBRow.unapply)
}