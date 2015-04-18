package controllers

import controllers.validator.BaseCrudValidator
import db.dao.BaseCrudDao
import models.BaseEntity
import models.Common._
import models.dto.JsonDtoService
import play.api.libs.json.JsArray

abstract class BaseReadController[A <: BaseEntity] extends BaseController {

  protected val validator: BaseCrudValidator[A]
  protected val dto: JsonDtoService[A]
  protected val dao: BaseCrudDao[A]

  def find(routeIds: Id*) = serveHttpResponseWithDB {
    implicit rs =>
      dao.find(routeIds)(rs.dbSession).map { entity =>
        validator.validateGetRequest(entity)(rs.dbSession)
        Ok(dto.parse(entity).toJson)
      }.getOrElse(NotFound)
  }

  def findAll(routeIds: Id*) = serveHttpResponseWithDB {
    implicit rs =>
      validator.validateGetAllRequest(rs.dbSession)
      Ok(JsArray(dao.findAll(routeIds)(rs.dbSession)
        .map(entity => dto.parse(entity).toJson)))
  }
}
