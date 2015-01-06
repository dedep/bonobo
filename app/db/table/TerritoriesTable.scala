package db.table

import play.api.db.slick.Config.driver.simple._

case class TerritoryDBRow(id: Long, name: String, population: Long, containerId: Option[Long],
                          code: String, isCountry: Boolean, modifiable: Boolean)

case class NewTerritoryDBRow(name: String, population: Long, containerId: Option[Long],
                          code: String, isCountry: Boolean, modifiable: Boolean)

class TerritoriesTable(tag: Tag) extends Table[TerritoryDBRow](tag, "territories") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def population = column[Long]("population", O.NotNull)
  def containerId = column[Option[Long]]("container", O.Nullable)
  def code = column[String]("code", O.NotNull)
  def isCountry = column[Boolean]("is_country", O.NotNull)
  def modifiable = column[Boolean]("modifiable", O.NotNull)

  def * = (id, name, population, containerId, code, isCountry, modifiable) <> (TerritoryDBRow.tupled, TerritoryDBRow.unapply)
  def autoInc = (name, population, containerId, code, isCountry, modifiable) <>
    (NewTerritoryDBRow.tupled, NewTerritoryDBRow.unapply)
}
