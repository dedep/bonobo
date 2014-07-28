package models

import models.dedep.bonobo.core.team.Team
import play.api.db.slick.Config.driver.simple._

case class City(id: Long, override val name: String, population: Int, points: Int, container: Long)
  extends Team(population, points, name) {
}

class CitiesTable(tag: Tag) extends Table[City](tag, "cities") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def population = column[Int]("population", O.NotNull)
  def points = column[Int]("points",O.NotNull)
  def territoryId = column[Long]("container", O.NotNull)

  def * = (id, name, population, points, territoryId) <> (City.tupled, City.unapply)

  def territory = foreignKey("ter_fk", territoryId, TableQuery[TerritoriesTable])(_.id)
}