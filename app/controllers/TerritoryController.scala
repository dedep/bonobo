package controllers

import com.typesafe.scalalogging.slf4j.Logger
import controllers.validator.{BaseCrudValidator, TerritoryValidator}
import db.dao.BaseCrudDao
import db.dao.city.CityDao
import db.dao.territory.TerritoryDao
import db.dao.tournament.TournamentDao
import models.dto.{JsonDtoService, TerritoryDto}
import models.territory.Territory
import models.tournament.{GameRules, TournamentImpl}
import org.slf4j.LoggerFactory
import play.api.data.Forms._
import play.api.data._
import play.api.db.slick._
import play.api.libs.json.JsArray
import play.api.mvc.{Action, AnyContent, Result}
import scaldi.{Injectable, Injector}
import utils.FunLogger._

import scala.slick.jdbc.JdbcBackend

class TerritoryController(implicit inj: Injector) extends BaseCrudController[Territory] with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  private val cityDao = inject[CityDao]
  private val territoryDao = inject[TerritoryDao]
  private val tournamentDao = inject[TournamentDao]

  override protected val validator = inject[TerritoryValidator]
  override protected val dto = TerritoryDto //todo: inject
  override protected val dao = inject[TerritoryDao]

  def findByCode(code: String) = serveHttpResponseWithDB { implicit rs =>
    handleOptionalTerritory(territoryDao.find(code))
  }

  def startTournament(): Action[AnyContent] = serveHttpResponseWithDB { implicit rs =>
    case class StartTournamentFormData(name: String, code: String)

    val startTournamentForm = Form(
      mapping(
        "name" -> text,
        "code" -> text
      )(StartTournamentFormData.apply)(StartTournamentFormData.unapply)
    )

    startTournamentForm.bindFromRequest.fold(
      hasErrors => {
        log.error("Cannot bind Start Tournament Request " + hasErrors)
        BadRequest("Cannot bind Start Tournament Request")
      },
      success => {
        startTournament(success.code, success.name)
      }
    )
  }

  private def handleOptionalTerritory(t: Option[Territory]) = t match {
    case None => NotFound(views.html.error("Territory not found"))
    case Some(t: Territory) => Ok(views.html.territory(t))
  }

  private def startTournament(territoryCode: String, name: String)(implicit rs: JdbcBackend#Session): Result = {
    territoryDao.find(territoryCode) match {
      case None => NotFound(s"Territory with ID = $territoryCode does not exists.")
      case Some(t: Territory) =>
        val cities = cityDao.getAllWithinTerritoryCascade(t.id)
        if (cities.length < 2) {
          PreconditionFailed("Tournament requires at least 2 cities in territory.")
        }
        else {
          val rules = new GameRules(0, 1, 3)
          val newIndex = tournamentDao.saveNew(new TournamentImpl(t, cities, name)(rules))
          val successMsg = "Tournament with ID=" + newIndex + " has been created successfully."

          Ok(successMsg)
        }
    }
  }
}