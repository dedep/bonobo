package controllers

import db.dao.city.CityDao
import models.territory.City
import play.api.db.slick._
import scaldi.{Injectable, Injector}

class CityController(implicit inj: Injector) extends BaseController with Injectable {
  private val cityDao = inject[CityDao]

  def find(id: Long) = serveHttpResponseWithDB {
    implicit rs => {
      cityDao.fromId(id) match {
        case None => NotFound(views.html.error("City not found"))
        case Some(c: City) => Ok(views.html.city(c))
      }
    }
  }
}