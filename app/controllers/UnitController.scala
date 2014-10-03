package controllers

import db_access.dao.unit.UnitDao
import models.round.RoundUnit
import play.api.db.slick.DBAction
import play.api.mvc.Controller
import scaldi.{Injectable, Injector}

class UnitController(implicit inj: Injector) extends BaseController with Injectable {
  private val unitDao = inject[UnitDao]

  def find(id: Long) = wrapDBRequest {
    implicit rs =>
      unitDao.fromId(id)(rs.dbSession) match {
        case None => NotFound(views.html.error("Unit not found"))
        case Some(u: RoundUnit) => Ok(views.html.unit(u))
      }
  }
}