package controllers

import models.db_model.Match
import play.api.db.slick.DBAction
import play.api.mvc.Controller

object MatchController extends Controller {
  def find(id: Long) = DBAction {
    implicit rs =>
      Match.fromId(id) match {
        case None => NotFound("Match not found")
        case Some(m: models.core._match.Match) =>
          Ok("Match, \n" + m.toString + m.getClass)
      }
  }
}
