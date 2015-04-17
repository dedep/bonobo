package db.table

import db.row.TerritoryDBRow
import models.territory.Territory
import play.api.db.slick.Config.driver.simple._
import scaldi.Injector


case class NewTerritoryDBRow(code: String, name: String, population: Long, container: Option[Long],
                          isCountry: Boolean, modifiable: Boolean)

class TerritoriesTable(tag: Tag)(implicit inj: Injector) extends BaseTable[Territory, TerritoryDBRow](tag, "territories") {
  def code = column[String]("code", O.NotNull)
  def name = column[String]("name", O.NotNull)
  def population = column[Long]("population", O.NotNull)
  def container = column[Option[Long]]("container", O.Nullable)
  def isCountry = column[Boolean]("is_country", O.NotNull)
  def modifiable = column[Boolean]("modifiable", O.NotNull)

  def * = (id, code, name, population, container, isCountry, modifiable) <> ((TerritoryDBRow.apply _).tupled, TerritoryDBRow.unapply)
  def autoInc = (code, name, population, container, isCountry, modifiable) <> (NewTerritoryDBRow.tupled, NewTerritoryDBRow.unapply)
}
