package controllers

import models.core.round.RoundUnit
import models.db_model.Unit
import play.api.db.slick.DBAction
import play.api.mvc.Controller

object UnitController extends Controller {
  def find(id: Long) = DBAction {
    implicit rs =>
      Unit.fromId(id) match {
        case None => NotFound("Unit not found")
        case Some(u: RoundUnit) =>
          Ok("Unit, \n" + u)
      }
  }
}