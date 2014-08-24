package models.db_model

import models.core.team.Team
import models.table.CitiesTable
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.mvc.AnyContent

class City(override val id: Long, override val name: String, val population: Int, val points: Int, container: => Territory)
  extends Team(id, population, points, name) with Containable {

  lazy val territory = container

  override lazy val getContainers: List[Territory] =
    appendParentContainers(territory :: Nil)
}

object City {
  val ds = TableQuery[CitiesTable]

  def fromId(id: Long)(implicit rs: DBSessionRequest[AnyContent]): Option[City] =
    (for (city <- ds if city.id === id) yield city).firstOption match {
      case None => None
      case Some((name: String, population: Int, points: Int, container: Long)) =>
        Some(new City(id, name, population, points, Territory.fromId(container)
          .getOrElse(throw new IllegalStateException("City " + name + " references to non-existent territory"))))
    }
}

