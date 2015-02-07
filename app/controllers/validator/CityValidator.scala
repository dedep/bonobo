package controllers.validator

import models.territory.City

import scala.slick.jdbc.JdbcBackend

class CityValidator extends BaseCrudValidator[City] {
  override def validateEditRequest(serverSideEntity: City, frontSideEntity: City)(implicit rs: JdbcBackend#Session): Unit = {
    if (!serverSideEntity.territory.modifiable)
      throw new ValidationException("City is not modifiable")
    else if (frontSideEntity.points != serverSideEntity.points)
      throw new ValidationException("Cannot edit cities points")
  }

  override def validateCreateRequest(frontSideEntity: City)(implicit rs: JdbcBackend#Session): Unit = {
    if (frontSideEntity.points != 0) {
      throw new ValidationException("Cities points modification is forbidden")
    }
  }

  override def validateDeleteRequest(serverSideEntity: City)(implicit rs: JdbcBackend#Session): Unit = {
    if (!serverSideEntity.territory.modifiable)
      throw new ValidationException("City is not modifiable")
  }

  override def validateGetRequest(serverSideEntity: City)(implicit rs: JdbcBackend#Session): Unit = {}

  override def validateGetAllRequest(implicit rs: JdbcBackend#Session): Unit = {}
}
