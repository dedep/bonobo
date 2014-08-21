package controllers

import models.core.tournament.Tournament
import models.db_model.{CitiesTournaments, Tournament}
import play.api.db.slick._
import play.api.mvc._

object TournamentController extends Controller {
  def find(id: Long) = DBAction { implicit rs =>
    Tournament.fromId(id) match {
      case None => NotFound(views.html.error("Tournament not found"))
      case Some(t: (Tournament)) => Ok(views.html.tournament(t))
    }
  }

  def processNextStep(id: Long) = DBAction { implicit rs =>
      Tournament.fromId(id) match {
        case None => NotFound(views.html.error("Tournament not found"))
        case Some(t: (Tournament)) => {
          if (!t.isFinished()) {
            val nTournament = t.doStep()
            Tournament.update(nTournament)
          }
          Ok
        }
      }
    }
}