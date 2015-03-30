package controllers

import controllers.validator.BaseCrudValidator
import db.dao.BaseCrudDao
import models.dto.JsonDtoService
import play.api.libs.json.JsArray

abstract class BaseCrudController[A] extends BaseController {
  protected val validator: BaseCrudValidator[A]
  protected val dto: JsonDtoService[A]
  protected val dao: BaseCrudDao[A]

  def find(id: Long) = serveHttpResponseWithDB {
    implicit rs =>
      dao.find(id)(rs.dbSession).map { entity =>
        validator.validateGetRequest(entity)(rs.dbSession)
        Ok(dto.parse(entity).toJson)
      }.getOrElse(NotFound)
  }

  def findAll() = serveHttpResponseWithDB {
    implicit rs =>
      validator.validateGetAllRequest(rs.dbSession)
      Ok(JsArray(dao.findAll(rs.dbSession).map(entity => dto.parse(entity).toJson)))
  }

  def create() = serveHttpResponseWithTransactionalDB {
    implicit rs => {
      dto.form.bindFromRequest.fold(
        hasErrors => {
          BadRequest("Cannot bind model from request " + hasErrors)
        },
        success => {
          val obj = success.toObject
          validator.validateCreateRequest(obj)(rs.dbSession)
          val id = dao.save(obj)(rs.dbSession)

          Ok(s"Entity with id: $id has been successfully created.")
        }
      )
    }
  }

  def delete(id: Long) = serveHttpResponseWithTransactionalDB {
    implicit rs => {
      dao.find(id)(rs.dbSession).map(obj => {
        validator.validateDeleteRequest(obj)(rs.dbSession)
        dao.delete(obj)(rs.dbSession)
        Ok(s"Entity [id=$id] has been deleted.")
      }).getOrElse(BadRequest(s"Cannot delete non-existent entity: [id=$id]"))
    }
  }

  def edit(id: Long) = serveHttpResponseWithTransactionalDB {
    implicit rs => {
      dao.find(id)(rs.dbSession).map(obj => {
        dto.form.bindFromRequest.fold(
          hasErrors => {
            BadRequest(s"Cannot bind entity from request $hasErrors")
          },
          success => {
            validator.validateEditRequest(obj, success.toObject)(rs.dbSession)
            dao.update(success.toObject, id)(rs.dbSession)
            Ok(s"Entity with id: $id has been successfully edited.")
          }
        )
      }).getOrElse(BadRequest(s"Cannot edit non-existent entity: $id"))
    }
  }
}
