package controllers

import com.typesafe.scalalogging.slf4j.Logger
import models.db_model.{Tournament, Territory, CitiesTournaments}
import org.slf4j.LoggerFactory
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current

object TerritoryController extends Controller {
  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  def find(id: Long) = DBAction { implicit rs =>
    Territory.fromId(id) match {
      case None => NotFound(views.html.error("Territory not found"))
      case Some(t: Territory) => Ok(views.html.territory(t))
    }
  }

  def startTournament(id: Long) = DBAction { implicit rs =>
    Territory.fromId(id) match {
      case None => NotFound("Territory not found")
      case Some(t: Territory) =>
        val cities = Territory.getAllChildrenCities(t)
        if (cities.length < 2) {
          PreconditionFailed("Tournament requires at least 2 cities in territory")
        }
        else {
          val newIndex = Tournament.ds.foldLeft(0l){ Math.max } + 1
          Tournament.ds.forceInsert(newIndex)
          CitiesTournaments.autoIncInsert
            .insertAll(cities.map(city => (city.id, newIndex)):_*)

          Ok("Tournament with ID=" + newIndex + " has been created successfully")
        }
    }
  }
}