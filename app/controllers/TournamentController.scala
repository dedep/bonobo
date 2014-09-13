package controllers

import models.core.tournament.Tournament
import models.db_model.Tournament
import play.api.db.slick._
import play.api.mvc._
import utils.AlertsHelper._

object TournamentController extends Controller {
  def find(id: Long) = DBAction { implicit rs =>
    Tournament.fromId(id) match {
      case None => NotFound(views.html.error("Tournament not found"))
      case Some(t: (Tournament)) => Ok(views.html.tournament(t)(None))
    }
  }

  def processNextStep(id: Long) = DBAction { implicit rs =>
      Tournament.fromId(id) match {
        case None => NotFound(views.html.error("Tournament not found"))
        case Some(t: (Tournament)) => {
          try {
            if (!t.isFinished()) {
              val nTournament = t.doStep()
              val nTournamentId = Tournament.saveOrUpdate(nTournament)

              Ok(views.html.tournament(Tournament.fromId(nTournamentId).get)(success("Tournament processing finished successfully.")))
            } else {
              PreconditionFailed(views.html.tournament(t)(fail("Tournament is already finished.")))
            }
          } catch {
            case e: Exception =>
              //todo: logi
              InternalServerError(views.html.tournament(t)(fail("Error occurred while processing tournament: " + e)))
          }
        }
      }
    }
}