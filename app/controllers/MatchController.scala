package controllers

import models.db_model.Match
import play.api.db.slick.DBAction
import play.api.mvc.Controller

object MatchController extends Controller {
  def find(id: Long) = DBAction {
    implicit rs =>
      Match.fromId(id) match {
        case None => NotFound(views.html.error("Match not found"))
        case Some(m: models.core._match.Match) => Ok(views.html.mmatch(m))
      }
  }
}
