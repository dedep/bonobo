package controllers

import db_access.dao.city.CityDao
import db_access.dao.territory.TerritoryDao
import play.api.Routes
import play.api.mvc.Action
import scaldi.{Injector, Injectable}
import service.city_updater.CityUpdater

class AppController(implicit inj: Injector) extends BaseController with Injectable {
  private val territoryController = inject[TerritoryController]
  private val cityUpdater = inject[CityUpdater]
  private val cityDao = inject[CityDao]
  private val territoryDao = inject[TerritoryDao]

  def world = territoryController.findByCode("W")

  def updateCitiesDefinitions() = wrapDBRequest {
    implicit rs =>
      implicit val dbSession = rs.dbSession

      val notUpdatedCount = cityDao.getAllWithinTerritoryCascade(territoryDao.fromCode("W").get)
        .map(city => cityUpdater.update(city))
        .count(_ == false)

      Ok(notUpdatedCount.toString)
  }

  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        TerritoryController.startTournament, TournamentController.processNextStep
      )
    ).as("text/javascript")
  }
}
