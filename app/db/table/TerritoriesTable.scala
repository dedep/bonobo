package db.table

import db.row.model.TerritoryRow
import models.territory.Territory
import play.api.db.slick.Config.driver.simple._
import scaldi.Injector

class TerritoriesTable(tag: Tag)(implicit inj: Injector) extends BaseTable[Territory, TerritoryRow](tag, "territories") {
  def code = column[String]("code", O.NotNull)
  def name = column[String]("name", O.NotNull)
  def population = column[Long]("population", O.NotNull)
  def container = column[Option[Long]]("container", O.Nullable)
  def isCountry = column[Boolean]("is_country", O.NotNull)
  def modifiable = column[Boolean]("modifiable", O.NotNull)

  def * = (id, code, name, population, container, isCountry, modifiable) <> ((TerritoryRow.apply _).tupled, TerritoryRow.unapply)
}
