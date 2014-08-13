package controllers

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current

object AppController extends Controller {

  def index = DBAction { implicit rs =>
    Ok("DUPA")
  }
}
