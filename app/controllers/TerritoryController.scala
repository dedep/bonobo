package controllers

import com.typesafe.scalalogging.slf4j.Logger
import db_access.dao.city.CityDao
import db_access.dao.territory.TerritoryDao
import db_access.dao.tournament.TournamentDao
import models.territory.Territory
import models.tournament.TournamentImpl
import org.slf4j.LoggerFactory
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick._
import play.api.mvc.{Action, AnyContent, Result}
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend

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
    case Some(t: Territory) => Ok(views.html.territory(t))
  }

  def startTournament(): Action[AnyContent] = wrapDBRequest { implicit rs =>
    case class StartTournamentFormData(name: String, id: Int)

    val startTournamentForm = Form(
      mapping(
        "name" -> text,
        "id" -> number
      )(StartTournamentFormData.apply)(StartTournamentFormData.unapply)
    )

    startTournamentForm.bindFromRequest.fold(
      hasErrors => {
        log.error("Cannot bind Start Tournament Request " + hasErrors)
        BadRequest("Cannot bind Start Tournament Request")
      },
      success => {
        startTournament(success.id, success.name)
      }
    )
  }

  private def startTournament(territoryId: Int, name: String)(implicit rs: JdbcBackend#Session): Result = {
    territoryDao.fromId(territoryId) match {
      case None => NotFound(views.html.error("Territory not found"))
      case Some(t: Territory) =>
        val cities = cityDao.getAllWithinTerritoryCascade(t)
        if (cities.length < 2) {
          PreconditionFailed(views.html.error("Tournament requires at least 2 cities in territory."))
        }
        else {
          val newIndex = tournamentDao.saveNew(new TournamentImpl(cities, name))
          val successMsg = "Tournament with ID=" + newIndex + " has been created successfully."

          Ok(successMsg)
        }
    }
  }
}