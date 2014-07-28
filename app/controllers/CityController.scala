package controllers

import models.{City, CitiesTable}
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current
import scala.slick.lifted.TableQuery

object CityController extends Controller {
  val cities = TableQuery[CitiesTable]

  def find(id: Long) = DBAction {
    implicit rs =>
      (for (city <- cities if city.id === id) yield city).firstOption match {
        case None => NotFound("City not found")
        case Some(c: City) => Ok(views.html.city(c))
      }
  }
}