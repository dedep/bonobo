package controllers

import models.core.round.Round
import models.db_model.Round
import play.api.db.slick.DBAction
import play.api.mvc.Controller

object RoundController extends Controller {

  def find(id: Long) = DBAction {
    implicit rs =>
      Round.fromId(id) match {
        case None => NotFound("Round not found")
        case Some(r: Round) => Ok("Round, \ncities: " + r.teams.map(_.name).mkString(", ") + "\nstep: " + r.stepIndex)
      }
  }

}
