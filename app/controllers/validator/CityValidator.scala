package controllers.validator

import models.territory.City

import scala.slick.jdbc.JdbcBackend

class CityValidator {
  def validateEditRequest(serverSideEntity: City, frontSideEntity: City)(implicit rs: JdbcBackend#Session): Unit =
    if (frontSideEntity.territory.isWorld)
      throw new ValidationException("Cannot add city directly to World")
    else if (!serverSideEntity.territory.modifiable)
      throw new ValidationException("City is not modifiable")
    else if (frontSideEntity.points != serverSideEntity.points)
      throw new ValidationException("Cannot edit cities points")
    else if (frontSideEntity.population <= 0)
      throw new ValidationException("Illegal population number")

  def validateCreateRequest(frontSideEntity: City)(implicit rs: JdbcBackend#Session): Unit = {
    if (frontSideEntity.territory.isWorld)
      throw new ValidationException("Cannot add city directly to World")
    if (frontSideEntity.points != 0)
      throw new ValidationException("Cities points modification is forbidden")
    else if (frontSideEntity.population <= 0)
      throw new ValidationException("Illegal population number")
  }

  def validateDeleteRequest(serverSideEntity: City)(implicit rs: JdbcBackend#Session): Unit = {
    if (!serverSideEntity.territory.modifiable)
      throw new ValidationException("City is not modifiable")
  }

  def validateGetRequest(serverSideEntity: City)(implicit rs: JdbcBackend#Session): Unit = {}

  def validateGetAllRequest(implicit rs: JdbcBackend#Session): Unit = {}
}
