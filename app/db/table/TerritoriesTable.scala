package db.table

import play.api.db.slick.Config.driver.simple._

case class TerritoryDBRow(code: String, name: String, population: Long, containerCode: Option[String],
                          isCountry: Boolean, modifiable: Boolean)

class TerritoriesTable(tag: Tag) extends Table[TerritoryDBRow](tag, "territories") {
  def code = column[String]("code", O.NotNull)
  def name = column[String]("name", O.NotNull)
  def population = column[Long]("population", O.NotNull)
  def containerCode = column[Option[String]]("container", O.Nullable)
  def isCountry = column[Boolean]("is_country", O.NotNull)
  def modifiable = column[Boolean]("modifiable", O.NotNull)

  def * = (code, name, population, containerCode, isCountry, modifiable) <> (TerritoryDBRow.tupled, TerritoryDBRow.unapply)
}
