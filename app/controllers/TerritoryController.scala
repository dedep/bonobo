package controllers

import com.typesafe.scalalogging.slf4j.Logger
import db.dao.city.CityDao
import db.dao.territory.TerritoryDao
import db.dao.tournament.TournamentDao
import models.dto.TerritoryDto
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

class TerritoryController(implicit inj: Injector) extends BaseController with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  private val cityDao = inject[CityDao]
  private val territoryDao = inject[TerritoryDao]
  private val tournamentDao = inject[TournamentDao]

//  JSON API
  def findByCodeAsJson(code: String) = serveHttpResponseWithDB { implicit rs =>
    territoryDao.fromCode(code)
      .map(t => TerritoryDto.parse(t))
      .map(t => Ok(t.toJson))
      .getOrElse(NotFound(code))
  }

  def findAllAsJson() = serveHttpResponseWithDB { implicit rs =>
    Ok(JsArray(territoryDao.findAll()
      .map(t => TerritoryDto.parse(t).toJson))
    )
  }

  def create() = serveHttpResponseWithTransactionalDB { implicit rs =>
    bindRequestAndPerform { t =>
      territoryDao.save(t)(rs.dbSession)
    }(rs.dbSession)
  }

  def edit(oldCode: String) = serveHttpResponseWithTransactionalDB { implicit rs =>
    modifyTerritory(oldCode)(rs.dbSession) { () =>
      bindRequestAndPerform { t =>
        territoryDao.update(t, oldCode)(rs.dbSession)
      }(rs.dbSession)
    }
  }

  def delete(code: String) = serveHttpResponseWithTransactionalDB { implicit rs =>
    val currentTerritory = territoryDao.fromCode(code)
    modifyTerritory(code)(rs.dbSession) { () =>
      performTerritoryAction { t =>
        territoryDao.delete(t)(rs.dbSession)
      }(currentTerritory.get)(rs.dbSession)
    }
  }

  private def modifyTerritory(code: String)(rs: JdbcBackend#Session)(action: () => Result)
                             (implicit request : play.api.mvc.Request[_]): Result = {
      val currentTerritory = territoryDao.fromCode(code)(rs)
      currentTerritory.map(t => {
        if (!t.modifiable) BadRequest("Cannot modify unmodifiable territory.")
        else action()
      }).getOrElse(NotFound("Cannot modify non-existent territory."))
    }

  private def bindRequestAndPerform(f: Territory => Unit)(rs: JdbcBackend#Session)
                                         (implicit request : play.api.mvc.Request[_]): Result = {
    TerritoryDto.form.bindFromRequest.fold(
        hasErrors => {
          BadRequest("Cannot bind territory from request.")
            .plainLog("Binding territory error " + hasErrors).warn()
        },
        success => {
          val parentTerritoryStub = success.parent.map(p => new Territory(p.id, "", 0, None, "", false, false))
          val t = new Territory(success.id, success.name, success.population, parentTerritoryStub,
            success.code, success.isCountry, true)

          performTerritoryAction(f)(t)(rs)
        }
      )
  }

  private def performTerritoryAction(f: Territory => Unit)(t: Territory)(rs: JdbcBackend#Session)
                                    (implicit request : play.api.mvc.Request[_]): Result = {
    f.apply(t)
    Ok("Territory action has been successfully performed.")
  }
  //  END: JSON API


  def findByCode(code: String) = serveHttpResponseWithDB { implicit rs =>
    handleOptionalTerritory(territoryDao.fromCode(code))
  }


  def startTournament(): Action[AnyContent] = serveHttpResponseWithDB { implicit rs =>
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

  def find(id: Long) = serveHttpResponseWithDB { implicit rs =>
    handleOptionalTerritory(territoryDao.fromId(id))
  }

  private def handleOptionalTerritory(t: Option[Territory]) = t match {
    case None => NotFound(views.html.error("Territory not found"))
    case Some(t: Territory) => Ok(views.html.territory(t))
  }

  private def startTournament(territoryId: Int, name: String)(implicit rs: JdbcBackend#Session): Result = {
    territoryDao.fromId(territoryId) match {
      case None => NotFound("Territory with ID = " + territoryId + " does not exists.")
      case Some(t: Territory) =>
        val cities = cityDao.getAllWithinTerritoryCascade(t)
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