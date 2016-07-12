package controllers

import com.typesafe.scalalogging.slf4j.Logger
import controllers.validator.TerritoryValidator
import db.dao.{CityDao, TerritoryDao, TournamentDao}
import dto.mapper.TerritoryDtoMapper
import models.Common._
import models.territory.Territory
import models.tournament.{GameRules, TournamentImpl}
import org.slf4j.LoggerFactory
import play.api.data.Forms._
import play.api.data._
import play.api.db.slick._
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.{Action, AnyContent, Result}
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend

class TerritoryController(implicit inj: Injector) extends BaseController with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  private val cityDao = inject[CityDao]
  private val territoryDao = inject[TerritoryDao]
  private val tournamentDao = inject[TournamentDao]

  protected val validator = inject[TerritoryValidator]
  protected val dto = inject[TerritoryDtoMapper]
  protected val dao = inject[TerritoryDao]

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
        val cities = cityDao.getAllWithinTerritoryCascade(t.id.get)
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

  def findTerritoryByCode(code: String) = serveHttpResponseWithDB {
    implicit rs =>
      dao.find(code)(rs.dbSession).map { entity =>
        validator.validateGetRequest(entity)(rs.dbSession)
        Ok(dto.parse(entity).toJson)
      }.getOrElse(NotFound)
  }

  def create(routeIds: Id*) = serveHttpResponseWithTransactionalDB {
    implicit rs => {
      dto.form.bindFromRequest.fold(
        hasErrors => {
          BadRequest("Cannot bind model from request " + hasErrors)
        },
        success => {
          val obj = success.toObject
          validator.validateCreateRequest(obj)(rs.dbSession)
          val id = dao.insert(obj)(rs.dbSession)

          Ok(Json.obj("id" -> id))
        }
      )
    }
  }

  def delete(routeIds: Id*) = serveHttpResponseWithTransactionalDB {
    implicit rs => {
      dao.find(routeIds)(rs.dbSession).map(obj => {
        validator.validateDeleteRequest(obj)(rs.dbSession)
        dao.delete(obj)(rs.dbSession)
        Ok(s"Entity [id=${routeIds.last}] has been deleted.")
      }).getOrElse(BadRequest(s"Cannot delete non-existent entity: [id=${routeIds.last}]"))
    }
  }

  def edit(routeIds: Id*) = serveHttpResponseWithTransactionalDB {
    implicit rs => {
      dao.find(routeIds)(rs.dbSession).map(obj => {
        dto.form.bindFromRequest.fold(
          hasErrors => {
            BadRequest(s"Cannot bind entity from request $hasErrors")
          },
          success => {
            validator.validateEditRequest(obj, success.toObject)(rs.dbSession)
            dao.update(success.toObject, routeIds.last)(rs.dbSession)
            Ok(s"Entity with id: ${routeIds.last} has been successfully edited.")
          }
        )
      }).getOrElse(BadRequest(s"Cannot edit non-existent entity: ${routeIds.last}"))
    }
  }

  def find(routeIds: Id*) = serveHttpResponseWithDB {
    implicit rs =>
      dao.find(routeIds)(rs.dbSession).map { entity =>
        validator.validateGetRequest(entity)(rs.dbSession)
        Ok(dto.parse(entity).toJson)
      }.getOrElse(NotFound)
  }

  def findAll(routeIds: Id*) = serveHttpResponseWithDB {
    implicit rs =>
      validator.validateGetAllRequest(rs.dbSession)
      Ok(JsArray(dao.findAll(routeIds)(rs.dbSession)
        .map(entity => dto.parse(entity).toJson)))
  }
}