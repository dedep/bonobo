package controllers.validator

import db.dao.TerritoryDao
import models.territory.Territory
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend

//todo: tests
class TerritoryValidator(implicit inj: Injector) extends Injectable {

  private val territoryDao = inject[TerritoryDao]

  def validateEditRequest(serverSideEntity: Territory, frontSideEntity: Territory)(implicit rs: JdbcBackend#Session): Unit =
    if (!serverSideEntity.modifiable)
      throw new ValidationException(s"Territory is not modifiable")
    else if (isTerritoryStructureLooped(serverSideEntity, frontSideEntity))
      throw new ValidationException(s"Cannot set territories parent that is already its child")
    else if (serverSideEntity.code != frontSideEntity.code && territoryCodeExists(frontSideEntity.code))
      throw new ValidationException(s"Territory with code=${frontSideEntity.code} already exists")
    else if (frontSideEntity.population <= 0)
      throw new ValidationException("Illegal population number")

  def validateCreateRequest(frontSideEntity: Territory)(implicit rs: JdbcBackend#Session): Unit =
    if (territoryCodeExists(frontSideEntity.code))
      throw new ValidationException(s"Territory with code=${frontSideEntity.code} already exists")
    else if (frontSideEntity.population <= 0)
      throw new ValidationException("Illegal population number")

  def validateDeleteRequest(serverSideEntity: Territory)(implicit rs: JdbcBackend#Session): Unit =
    if (!serverSideEntity.modifiable)
      throw new ValidationException(s"Territory is not modifiable")

  def validateGetRequest(serverSideEntity: Territory)(implicit rs: JdbcBackend#Session): Unit = {}

  def validateGetAllRequest(implicit rs: JdbcBackend#Session): Unit = {}

  private def isTerritoryStructureLooped(serverTerritory: Territory, frontTerritory: Territory)(implicit rs: JdbcBackend#Session): Boolean =
    frontTerritory.container.exists(parent => {
      territoryDao.getAllWithinTerritoryCascade(serverTerritory.id.get).contains(parent)
    })

  private def territoryCodeExists(territoryCode: String)(implicit rs: JdbcBackend#Session): Boolean =
    territoryDao.find(territoryCode).nonEmpty
}
