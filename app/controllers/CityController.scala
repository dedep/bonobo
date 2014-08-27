package controllers

import models.db_model.City
import play.api.db.slick._
import play.api.mvc._
import play.api.Play.current

object CityController extends Controller {

  //todo: trykacze na wszystkich kontrolerach
  def find(id: Long) = DBAction {
    implicit rs =>
      City.fromId(id) match {
        case None => NotFound(views.html.error("City not found"))
        case Some(c: City) => Ok(views.html.city(c))
      }
  }
}