package controllers

import models.core.round.Round
import models.db_model.Round
import play.api.db.slick.DBAction
import play.api.mvc.Controller

object RoundController extends Controller {

  def find(id: Long) = DBAction {
    implicit rs =>
      Round.fromId(id)(rs.dbSession) match {
        case None => NotFound(views.html.error("Round not found"))
        case Some(r: Round) => Ok(views.html.round(r))
      }
  }
}