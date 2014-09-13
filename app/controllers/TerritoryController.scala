package controllers

import com.typesafe.scalalogging.slf4j.Logger
import models.core.tournament.TournamentImpl
import models.db_model.{Tournament, Territory}
import org.slf4j.LoggerFactory
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current
import utils.AlertsHelper._

object TerritoryController extends Controller {
  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  //todo: powtórzony kod
  def find(id: Long) = DBAction { implicit rs =>
    Territory.fromId(id) match {
      case None => NotFound(views.html.error("Territory not found"))
      case Some(t: Territory) => Ok(views.html.territory(t)(None))
    }
  }

  def findByCode(id: String) = DBAction { implicit rs =>
    Territory.fromCode(id) match {
      case None => NotFound(views.html.error("Territory not found"))
      case Some(t: Territory) => Ok(views.html.territory(t)(None))
    }
  }

  def startTournament(id: Long) = DBAction { implicit rs =>
    Territory.fromId(id) match {
      case None => NotFound(views.html.error("Territory not found"))
      case Some(t: Territory) =>
        val cities = Territory.getAllChildrenCities(t)
        if (cities.length < 2) {
          PreconditionFailed(views.html.territory(t)
            (fail("Tournament requires at least 2 cities in territory."))
          )
        }
        else {
          val newIndex = Tournament.saveOrUpdate(new TournamentImpl(cities, "Tities Tournament"))
          val successMsg = success("Tournament with ID=" + newIndex + " has been created successfully.")

          Ok(views.html.territory(t)(successMsg))
        }
    }
  }
}