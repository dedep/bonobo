package controllers

import controllers.validator.{CityValidator, BaseCrudValidator}
import db.dao.BaseCrudDao
import db.dao.city.CityDao
import models.dto.{CityDto, JsonDtoService}
import models.territory.City
import scaldi.{Injectable, Injector}

class CityController(implicit inj: Injector) extends BaseCrudController[City] with Injectable {
  override protected val validator: BaseCrudValidator[City] = inject[CityValidator]
  override protected val dao: BaseCrudDao[City] = inject[CityDao]
  override protected val dto: JsonDtoService[City] = CityDto

  def findCity(id: Long) = serveHttpResponseWithDB {
    implicit rs => {
      dao.find(id :: Nil)(rs.dbSession) match {
        case None => NotFound(views.html.error("City not found"))
        case Some(c: City) => Ok(views.html.city(c))
      }
    }
  }
}