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

  def findAsJson(id: Long, territoryCode: String) = serveHttpResponseWithDB {
    implicit rs => {
      cityDao.fromId(id)
        .map(c => CityDto.parse(c))
        .map(c => Ok(c.toJson))
        .getOrElse(NotFound(id.toString))
    }
  }

  def findAll(territoryCode: String) = serveHttpResponseWithDB {
    implicit rs => {
      Ok(JsArray(cityDao.getAllWithinTerritoryCascade(territoryCode)
        .map(c => CityDto.parse(c))
        .map(c => c.toJson)))
    }
  }

  def save(territoryId: String) = serveHttpResponseWithTransactionalDB {
    implicit rs => {
      CityDto.form.bindFromRequest.fold(
        hasErrors => {
          BadRequest("Cannot bind City from request " + hasErrors)
        },
        success => {
          if (success.points == 0) {
            val id = cityDao.save(success.toObject)
            Ok(s"City with id: $id has been successfully created.")
          } else {
            BadRequest("Cities points modification is forbidden")
          }
        }
      )
    }
  }

  def delete(territoryCode: String, cityId: Long) = serveHttpResponseWithTransactionalDB {
    implicit rs => {
      cityDao.fromId(cityId).map(c => {
        if (c.territory.modifiable) {
          cityDao.delete(c)
          Ok(s"City $cityId has been deleted")
        } else {
          BadRequest(s"Cannot delete non-modifiable city: $cityId")
        }
      }).getOrElse(BadRequest(s"Cannot delete non-existent city: $cityId"))
    }
  }

  //todo: unit tests
  def edit(territoryCode: String, cityId: Long) = serveHttpResponseWithTransactionalDB {
    implicit rs => {
      cityDao.fromId(cityId).map(c => {
        if (c.territory.modifiable) {
          CityDto.form.bindFromRequest.fold(
            hasErrors => {
              BadRequest(s"Cannot bind City from request $hasErrors")
            },
            success => {
              if (success.points == c.points) {
                val id = cityDao.update(success.toObject)
                Ok(s"City with id: $id has been successfully edited.")
              } else {
                BadRequest("Cities points modification is forbidden")
              }
            }
          )
        } else {
          BadRequest(s"Cannot edit non-modifiable city: $cityId")
        }
      }).getOrElse(BadRequest(s"Cannot edit non-existent city: $cityId"))
    }
  }
}