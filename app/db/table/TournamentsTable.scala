package db.table

import scala.slick.driver.PostgresDriver.simple._

case class TournamentDBRow(id: Long, name: String, status: String, territoryCode: String)
case class NewTournamentDBRow(name: String, status: String, territoryCode: String)

class TournamentsTable(tag: Tag) extends Table[TournamentDBRow](tag, "tournaments") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def status = column[String]("status", O.NotNull)
  def territoryCode = column[String]("territory_code", O.NotNull)

  def * = (id, name, status, territoryCode) <> (TournamentDBRow.tupled, TournamentDBRow.unapply)
  def autoInc = (name, status, territoryCode) <> (NewTournamentDBRow.tupled, NewTournamentDBRow.unapply)
}