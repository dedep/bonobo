package models.db_model

import models.table.TerritoriesTable
import play.api.db.slick._
import play.api.libs.json._
import play.api.mvc._

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend

//todo: jakiś mechanizm trzeba zdefiniować żeby nie zapętlić hierarchii terytoriów
class Territory(val id: Long, val name: String, val population: Long, parent: => Option[Territory], val code: String)
  extends Containable {

  lazy val container = parent

  override lazy val getContainers: List[Territory] = container match {
    case None => Nil
    case Some(territory: Territory) => appendParentContainers(territory :: Nil)
  }
}

object Territory {
  val ds = TableQuery[TerritoriesTable]

  //todo: powtórzony kod
  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[Territory] = {
    (for (territory <- ds if territory.id === id) yield territory).firstOption match {
      case None => None
      case Some((id: Long, name: String, population: Long, containerId: Option[Long], code: String)) =>
        containerId match {
          case None => Some(new Territory(id, name, population, None, code))
          case Some(cid: Long) => Some(new Territory(id, name, population, fromId(cid), code))
      }
    }
  }

  def fromCode(code: String)(implicit rs: JdbcBackend#Session): Option[Territory] = {
    (for (territory <- ds if territory.code === code) yield territory).firstOption match {
      case None => None
      case Some((id: Long, name: String, population: Long, containerId: Option[Long], code: String)) =>
        containerId match {
          case None => Some(new Territory(id, name, population, None, code))
          case Some(cid: Long) => Some(new Territory(id, name, population, fromId(cid), code))
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
    ds.filter(_.id === t.id).update(t.id, t.name, t.population, t.container.map(_.id), t.code)
  }

  implicit object TerritoryFormat extends Format[Territory] {

    def writes(t: Territory): JsValue = {
      val citySeq = Seq(
        "id" -> JsNumber(t.id),
        "name" -> JsString(t.name),
        "population" -> JsNumber(t.population),
        "container" -> t.container.map(writes).getOrElse(JsNull)
      )
      JsObject(citySeq)
    }

    def reads(json: JsValue): JsResult[Territory] = {
      JsSuccess(new Territory(1, "", 2, None, ""))
    }
  }
}

