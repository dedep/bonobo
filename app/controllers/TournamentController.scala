package controllers

import com.typesafe.scalalogging.slf4j.Logger
import db_access.dao.tournament.TournamentDao
import models.tournament.Tournament
import org.slf4j.LoggerFactory
import play.api.db.slick._
import play.api.mvc._
import scaldi.{Injectable, Injector}
import utils.AlertsHelper._

class TournamentController(implicit inj: Injector) extends Controller with Injectable {
  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  private val tournamentDao = inject[TournamentDao]

  def find(id: Long) = DBAction { implicit rs =>
    tournamentDao.fromId(id) match {
      case None => NotFound(views.html.error("Tournament not found"))
      case Some(t: (Tournament)) => Ok(views.html.tournament(t)(None))
    }
  }

  def processNextStep(id: Long) = DBAction { implicit rs =>
    tournamentDao.fromId(id) match {
        case None => NotFound(views.html.error("Tournament not found"))
        case Some(t: (Tournament)) => {
          try {
            if (!t.isFinished()) {
              val nTournament = t.doStep()
              val nTournamentId = tournamentDao.saveOrUpdate(nTournament)

              Ok(views.html.tournament(tournamentDao.fromId(nTournamentId).get)(success("Tournament processing finished successfully.")))
            } else {
              PreconditionFailed(views.html.tournament(t)(fail("Tournament is already finished.")))
            }
          } catch {
            case e: Exception =>
              log.error("Error occurred while processing tournament: " + e + " ST: " + e.getStackTrace.mkString("\n"))
              InternalServerError(views.html.tournament(t)(fail("Error occurred while processing tournament: " + e)))
          }
        }
      }
    }
}