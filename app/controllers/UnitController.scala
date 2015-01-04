package controllers

import db.dao.unit.UnitDao
import models.unit.RoundUnit
import scaldi.{Injectable, Injector}

class UnitController(implicit inj: Injector) extends BaseController with Injectable {
  private val unitDao = inject[UnitDao]

  def find(id: Long) = performDBRequest {
    implicit rs =>
      unitDao.fromId(id)(rs.dbSession) match {
        case None => NotFound(views.html.error("Unit not found"))
        case Some(u: RoundUnit) => Ok(views.html.unit(u))
      }
  }
}