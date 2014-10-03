package controllers

import com.typesafe.scalalogging.slf4j.Logger
import db_access.dao.city.CityDao
import db_access.dao.territory.TerritoryDao
import db_access.dao.tournament.TournamentDao
import models.territory.Territory
import models.tournament.TournamentImpl
import org.slf4j.LoggerFactory
import play.api.db.slick._
import play.api.mvc._
import scaldi.{Injectable, Injector}
import utils.AlertsHelper._

class TerritoryController(implicit inj: Injector) extends BaseController with Injectable {
  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  private val cityDao = inject[CityDao]
  private val territoryDao = inject[TerritoryDao]
  private val tournamentDao = inject[TournamentDao]

  def find(id: Long) = wrapDBRequest { implicit rs =>
    handleOptionalTerritory(territoryDao.fromId(id))
  }

  def findByCode(code: String) = wrapDBRequest { implicit rs =>
    handleOptionalTerritory(territoryDao.fromCode(code))
  }

  private def handleOptionalTerritory(t: Option[Territory]) = t match {
    case None => NotFound(views.html.error("Territory not found"))
    case Some(t: Territory) => Ok(views.html.territory(t)(None))
  }

  def startTournament(id: Long) = wrapDBRequest { implicit rs =>
    territoryDao.fromId(id) match {
      case None => NotFound(views.html.error("Territory not found"))
      case Some(t: Territory) =>
        val cities = cityDao.getAllWithinTerritoryCascade(t)
        if (cities.length < 2) {
          PreconditionFailed(views.html.territory(t)
            (fail("Tournament requires at least 2 cities in territory."))
          )
        }
        else {
          val newIndex = tournamentDao.saveOrUpdate(new TournamentImpl(cities, "Custom name tournament"))
          val successMsg = success("Tournament with ID=" + newIndex + " has been created successfully.")

          Ok(views.html.territory(t)(successMsg))
        }
    }
  }
}