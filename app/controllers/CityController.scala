package controllers

import db_access.dao.city.CityDao
import models.territory.City
import play.api.libs.json._
import play.api.db.slick._
import scaldi.{Injector, Injectable}

class CityController(implicit inj: Injector) extends BaseController with Injectable {
  private val cityDao = inject[CityDao]

  def find(id: Long) = wrapDBRequest {
    implicit rs => {
      cityDao.fromId(id) match {
        case None => NotFound(views.html.error("City not found"))
        case Some(c: City) => Ok(views.html.city(c))
      }
    }
  }

  def findJson(id: Long) = wrapDBRequest {
      implicit rs =>
        cityDao.fromId(id) match {
          case None => NotFound(views.html.error("City not found"))
          case Some(c: City) => {
            Ok(Json.toJson(c))
          }
        }
    }
}