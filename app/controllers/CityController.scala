package controllers

import controllers.validator.{CityValidator, BaseCrudValidator}
import db.dao.BaseCrudDao
import db.dao.city.CityDao
import models.dto.{CityDto, JsonDtoService}
import models.territory.City
import play.api.mvc.{AnyContent, Action}
import scaldi.{Injectable, Injector}

class CityController(implicit inj: Injector) extends BaseCrudController[City, Long] with Injectable {
  override protected val validator: BaseCrudValidator[City] = inject[CityValidator]
  override protected val dao: BaseCrudDao[City, Long] = inject[CityDao]
  override protected val dto: JsonDtoService[City] = CityDto

  def findCity(id: Long) = serveHttpResponseWithDB {
    implicit rs => {
      dao.find(id)(rs.dbSession) match {
        case None => NotFound(views.html.error("City not found"))
        case Some(c: City) => Ok(views.html.city(c))
      }
    }
  }

  def delete(territoryCode: String, id: Long): Action[AnyContent] = super.delete(id)
  def create(territoryCode: String): Action[AnyContent] = super.create()
  def edit(territoryCode: String, id: Long): Action[AnyContent] = super.edit(id)
  def find(territoryCode: String, id: Long): Action[AnyContent] = super.find(id)
  def findAll(territoryCode: String): Action[AnyContent] = super.findAll()
}