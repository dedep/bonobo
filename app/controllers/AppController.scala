package controllers

import com.typesafe.scalalogging.slf4j.Logger
import db.dao.city.CityDao
import db.dao.territory.TerritoryDao
import org.slf4j.LoggerFactory
import play.api.Routes
import play.api.mvc.Action
import scaldi.{Injectable, Injector}
import service.city_updater.CityUpdater
import utils.FunLogger._

class AppController(implicit inj: Injector) extends BaseController with Injectable {
  private val territoryController = inject[TerritoryController]
  private val cityUpdater = inject[CityUpdater]
  private val cityDao = inject[CityDao]
  private val territoryDao = inject[TerritoryDao]

  def world = territoryController.findByCode("W")

  private implicit val log = Logger(LoggerFactory.getLogger(this.getClass))

  def updateCitiesDefinitions() = serveHttpResponseWithDB {
    implicit rs =>
      implicit val dbSession = rs.dbSession

      val notUpdatedCount = cityDao.getAllWithinTerritoryCascade(territoryDao.fromCode("W").get)
        .map(city => cityUpdater.update(city))
        .count(_ == false)
        .log(x => "Cities update request finished. Not updated cities: " + x).info()

      Ok(notUpdatedCount.toString)
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
      )
    ).as("text/javascript")
  }

  def preflight(all: String) = serveHttpResponse {
    Ok("").withHeaders(corsHeaders:_*)
  }
}
