package controllers

import controllers.validator.BaseCrudValidator
import db.dao.BaseCrudDao
import models.BaseEntity
import models.dto.JsonDtoService
import play.api.libs.json.JsArray
import models.Common._

abstract class BaseCrudController[A <: BaseEntity] extends BaseController {
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

  def create(routeIds: Id*) = serveHttpResponseWithTransactionalDB {
    implicit rs => {
      dto.form.bindFromRequest.fold(
        hasErrors => {
          BadRequest("Cannot bind model from request " + hasErrors)
        },
        success => {
          val obj = success.toObject
          validator.validateCreateRequest(obj)(rs.dbSession)
          val id = dao.insert(obj)(rs.dbSession)

          Ok(s"Entity with id: $id has been successfully created.")
        }
      )
    }
  }

  def delete(routeIds: Id*) = serveHttpResponseWithTransactionalDB {
    implicit rs => {
      dao.find(routeIds)(rs.dbSession).map(obj => {
        validator.validateDeleteRequest(obj)(rs.dbSession)
        dao.delete(obj)(rs.dbSession)
        Ok(s"Entity [id=${routeIds.last}] has been deleted.")
      }).getOrElse(BadRequest(s"Cannot delete non-existent entity: [id=${routeIds.last}]"))
    }
  }

  def edit(routeIds: Id*) = serveHttpResponseWithTransactionalDB {
    implicit rs => {
      dao.find(routeIds)(rs.dbSession).map(obj => {
        dto.form.bindFromRequest.fold(
          hasErrors => {
            BadRequest(s"Cannot bind entity from request $hasErrors")
          },
          success => {
            validator.validateEditRequest(obj, success.toObject)(rs.dbSession)
            dao.update(success.toObject, routeIds.last)(rs.dbSession)
            Ok(s"Entity with id: ${routeIds.last} has been successfully edited.")
          }
        )
      }).getOrElse(BadRequest(s"Cannot edit non-existent entity: ${routeIds.last}"))
    }
  }
}
