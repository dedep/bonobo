package models

import scala.slick.driver.PostgresDriver.simple._

case class Territory(id: Long, name: String, population: Long, container: Long) {

}

class TerritoriesTable(tag: Tag) extends Table[Territory](tag, "territories") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def population = column[Long]("population", O.NotNull)
  def containerId = column[Long]("container",O.Nullable)

  def * = (id, name, population, containerId) <> (Territory.tupled, Territory.unapply)

  def container = foreignKey("tter_fk", containerId, TableQuery[TerritoriesTable])(_.id)
  def citiesJoin = Cities.filter(_.territoryId === id).flatMap(_.territory)
}

