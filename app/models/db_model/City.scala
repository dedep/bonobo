package models.db_model

import models.core.team.Team
import models.table.CitiesTable
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.mvc.AnyContent

import scala.slick.jdbc.JdbcBackend

class City(override val id: Long, override val name: String, val population: Int, val points: Int, container: => Territory)
  extends Team(id, population, points, name) with Containable {

  lazy val territory = container

  override lazy val getContainers: List[Territory] =
    appendParentContainers(territory :: Nil)
}

object City {
  val ds = TableQuery[CitiesTable]

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[City] =
    (for (city <- ds if city.id === id) yield city).firstOption match {
      case None => None
      case Some((name: String, population: Int, points: Int, container: Long)) =>
        Some(new City(id, name, population, points, Territory.fromId(container)
          .getOrElse(throw new IllegalStateException("City " + name + " references to non-existent territory"))))
    }

  def saveOrUpdate(c: City)(implicit rs: JdbcBackend#Session): Long = {
    if (Territory.fromId(c.territory.id).isEmpty)
      throw new IllegalStateException("City cannot refer to non-existent territory")

    Territory.update(c.territory)

    if (fromId(c.id).isEmpty) save(c) else update(c)
  }

  private def save(c: City)(implicit rs: JdbcBackend#Session): Long =
    (ds returning ds.map(_.id)) += (c.name, c.population, c.points, c.territory.id)

  private def update(c: City)(implicit rs: JdbcBackend#Session): Long =
    ds.filter(_.id === c.id).update(c.name, c.population, c.points, c.territory.id)
}

