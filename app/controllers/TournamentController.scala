package controllers

import com.typesafe.scalalogging.slf4j.Logger
import db_access.dao.tournament.TournamentDao
import models.tournament.Tournament
import org.slf4j.LoggerFactory
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.mvc.{AnyContent, Action, Result}
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend

class TournamentController(implicit inj: Injector) extends BaseController with Injectable {
  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  private val tournamentDao = inject[TournamentDao]

  def find(id: Long) = wrapDBRequest { implicit rs =>
    tournamentDao.fromId(id) match {
      case None => NotFound(views.html.error("Tournament not found"))
      case Some(t: (Tournament)) => Ok(views.html.tournament(t))
    }
  }

  def processNextStep(): Action[AnyContent] = wrapDBRequest { implicit rs =>
    case class ProcessTournamentFormData(id: Int)

    val processTournamentForm = Form(
      mapping(
        "id" -> number
      )(ProcessTournamentFormData.apply)(ProcessTournamentFormData.unapply)
    )

    processTournamentForm.bindFromRequest.fold(
      hasErrors => {
        log.error("Cannot bind Process Tournament Request " + hasErrors)
        BadRequest("Cannot bind Process Tournament Request")
      },
      success => {
        log.info("Processing tournament with ID = " + success.id)
        processNextStep(success.id)
      }
    )
  }

  def processNextStep(id: Int)(implicit rs: JdbcBackend#Session): Result = {
    tournamentDao.fromId(id) match {
      case None => NotFound(views.html.error("Tournament not found"))
      case Some(t: (Tournament)) => {
        try {
          if (!t.isFinished()) {
            val nTournament = t.doStep()
            val nTournamentId = tournamentDao.saveOrUpdate(nTournament)

            Ok(views.html.tournament(tournamentDao.fromId(nTournamentId).get))
          } else {
            PreconditionFailed(views.html.error("Tournament is already finished."))
          }
        } catch {
          case e: Exception =>
            log.error("Error occurred while processing tournament: " + e + " ST: " + e.getStackTrace.mkString("\n"))
            InternalServerError(views.html.error("Error occurred while processing tournament: " + e))
        }
      }
    }
  }
}