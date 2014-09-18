package models.db_model

import com.typesafe.scalalogging.slf4j.Logger
import models.core.team.Team
import models.table.CitiesTable
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.libs.json._
import play.api.mvc.AnyContent

import scala.slick.jdbc.JdbcBackend

class City(override val id: Long, override val name: String, val population: Int, val points: Int, container: => Territory,
            val latitude: Double, val longitude: Double) extends Team(id, population, points, name) with Containable {

  lazy val territory = container

  override lazy val getContainers: List[Territory] =
    appendParentContainers(territory :: Nil)
}

object City {
  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  val ds = TableQuery[CitiesTable]

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[City] =
    (for (city <- ds if city.id === id) yield city).firstOption match {
      case None => None
      case Some((name: String, population: Int, points: Int, container: Long, latitude: Double, longitude: Double)) =>
        Some(new City(id, name, population, points, Territory.fromId(container)
          .getOrElse(throw new IllegalStateException("City " + name + " references to non-existent territory")), latitude, longitude))
    }

  def saveOrUpdate(c: City)(implicit rs: JdbcBackend#Session): Long = {
    if (Territory.fromId(c.territory.id).isEmpty)
      throw new IllegalStateException("City cannot refer to non-existent territory")

//    Territory.update(c.territory) TODO: na co to ??

    if (fromId(c.id).isEmpty) save(c) else update(c)
  }

  private def save(c: City)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Saving new city " + c)
    (ds returning ds.map(_.id)) += (c.name, c.population, c.points, c.territory.id, c.latitude, c.longitude)
  }

  private def update(c: City)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Updating city " + c)
    ds.filter(_.id === c.id).update(c.name, c.population, c.points, c.territory.id, c.latitude, c.longitude)
  }

  implicit object CityFormat extends Format[City] {

    def writes(city: City): JsValue = {
      val citySeq = Seq(
        "id" -> JsNumber(city.id),
        "name" -> JsString(city.name),
        "points" -> JsNumber(city.points),
        "population" -> JsNumber(city.population),
        "territory" -> Territory.TerritoryFormat.writes(city.territory)
      )
      JsObject(citySeq)
    }

    def reads(json: JsValue): JsResult[City] = {
      JsSuccess(new City(1, "", 1, 1, new Territory(1, "", 2, None, ""), 0d, 0d))
    }
  }
}

