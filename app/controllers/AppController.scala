package controllers

import play.Routes
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current

object AppController extends Controller {

  def world = TerritoryController.findByCode("W")

  def jsRoutes = Action { implicit request =>
    val js = Routes.javascriptRouter("appRoutes", controllers.routes.javascript.AppController.world)
    Ok(js).as("text/javascript")
  }
}
