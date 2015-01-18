package controllers

import db.dao.city.CityDao
import models.dto.CityDto
import models.territory.City
import play.api.db.slick._
import play.api.libs.json.JsArray
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

  def findAsJson(id: Long, territoryId: Long) = serveHttpResponseWithDB {
    implicit rs => {
      cityDao.fromId(id)
        .map(c => CityDto.parse(c))
        .map(c => Ok(c.toJson))
        .getOrElse(NotFound(id.toString))
    }
  }

  def findAll(territoryId: Long) = serveHttpResponseWithDB {
    implicit rs => {
      Ok(JsArray(cityDao.getAllWithinTerritoryCascade(territoryId)
        .map(c => CityDto.parse(c))
        .map(c => c.toJson)))
    }
  }
}