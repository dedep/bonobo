package controllers

import play.api.libs.json._
import models.db_model.City
import play.api.db.slick._
import play.api.mvc._
import play.api.Play.current

object CityController extends Controller {

  //todo:trykacze
  def find(id: Long) = DBAction {
    implicit rs =>
      City.fromId(id) match {
        case None => NotFound(views.html.error("City not found"))
        case Some(c: City) => Ok(views.html.city(c))
      }
  }

  def findJson(id: Long) = DBAction {
    implicit rs =>
      City.fromId(id) match {
        case None => NotFound(views.html.error("City not found"))
        case Some(c: City) => {
          Ok(Json.toJson(c))
        }
      }
  }
}