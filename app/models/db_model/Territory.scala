package models.db_model

import models.table.TerritoriesTable
import play.api.db.slick._
import play.api.mvc._

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend

//todo: jakiś mechanizm trzeba zdefiniować żeby nie zapętlić hierarchii terytoriów
class Territory(val id: Long, val name: String, val population: Long, parent: => Option[Territory])
  extends Containable {

  lazy val container = parent

  override lazy val getContainers: List[Territory] = container match {
    case None => Nil
    case Some(territory: Territory) => appendParentContainers(territory :: Nil)
  }
}

object Territory {
  val ds = TableQuery[TerritoriesTable]

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[Territory] = {
    (for (territory <- ds if territory.id === id) yield territory).firstOption match {
      case None => None
      case Some((id: Long, name: String, population: Long, containerId: Option[Long])) =>
        containerId match {
          case None => Some(new Territory(id, name, population, None))
          case Some(cid: Long) => Some(new Territory(id, name, population, fromId(cid)))
      }
    }
  }

  def getDirectCities(t: Territory)(implicit rs: JdbcBackend#Session): List[City] = {
      City.ds.filter(_.territoryId === t.id).map(_.id).list.map(City.fromId(_).get)
  }

  def getAllChildrenCities(t: Territory)(implicit rs: JdbcBackend#Session): List[City] = {
    val childrenTerritories = (for (territory <- Territory.ds if territory.containerId === t.id) yield territory)
      .list.map(a => Territory.fromId(a._1).get)

    childrenTerritories match {
      case Nil => getDirectCities(t)
      case _   => getDirectCities(t) ::: childrenTerritories.flatMap(getAllChildrenCities)
    }
  }

  def update(t: Territory)(implicit rs: JdbcBackend#Session): Long = {
    t.container.foreach(update)
    ds.filter(_.id === t.id).update(t.id, t.name, t.population, t.container.map(_.id))
  }
}

