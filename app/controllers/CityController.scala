package controllers

import controllers.validator.{CityValidator, BaseCrudValidator}
import db.dao.{CityDao, BaseCrudDao}
import dto.mapper.{CityDtoMapper, BaseDtoMapper}
import models.territory.City
import scaldi.{Injectable, Injector}

class CityController(implicit inj: Injector) extends BaseCrudController[City] with Injectable {
  override protected val validator: BaseCrudValidator[City] = inject[CityValidator]
  override protected val dao: BaseCrudDao[City] = inject[CityDao]
  override protected val dto: BaseDtoMapper[City] = inject[CityDtoMapper]

  def findCity(id: Long) = serveHttpResponseWithDB {
    implicit rs => {
      dao.find(id :: Nil)(rs.dbSession) match {
        case None => NotFound(views.html.error("City not found"))
        case Some(c: City) => Ok(views.html.city(c))
      }
    }
  }
}