package controllers

import com.typesafe.scalalogging.slf4j.Logger
import db.dao.tournament.TournamentDao
import models.tournament.Tournament
import org.slf4j.LoggerFactory
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick._
import play.api.mvc.{Action, AnyContent, Result}
import scaldi.{Injectable, Injector}
import utils.FunLogger._

import scala.slick.jdbc.JdbcBackend

class TournamentController(implicit inj: Injector) extends BaseController with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger(this.getClass))

  private val tournamentDao = inject[TournamentDao]

  def find(id: Long) = performDBRequest { implicit rs =>
    tournamentDao.fromId(id) match {
      case None => NotFound(views.html.error("Tournament not found"))
      case Some(t: (Tournament)) => Ok(views.html.tournament(t))
    }
  }

  def processNextStep(): Action[AnyContent] = performDBRequest { implicit rs =>
    case class ProcessTournamentFormData(id: Int)

    val processTournamentForm = Form(
      mapping("id" -> number)(ProcessTournamentFormData.apply)(ProcessTournamentFormData.unapply)
    )

    processTournamentForm.bindFromRequest.fold(
      hasErrors =>
        BadRequest("Cannot bind Process Tournament Request")
          .log(x => "Cannot bind Process Tournament Request " + hasErrors).error()
      ,
      success =>
        processNextStep(success.id)
          .log(x => "Processing tournament with ID = " + success.id).info()
    )
  }

  def processNextStep(id: Int)(implicit rs: JdbcBackend#Session): Result = {
    tournamentDao.fromId(id) match {
      case None => NotFound(views.html.error("Tournament not found"))
      case Some(t: (Tournament)) => {
        try {
          if (!t.isFinished) {
            log.info("Calculating match results in tournament " + id)
            val nTournament = t.doStep()
            val nTournamentId = tournamentDao.updateLastRound(nTournament)

            Ok(views.html.tournament(tournamentDao.fromId(nTournamentId).get))
              .log(x => "Succeed view rendering in tournament " + id).info()
          } else {
            log.warn("Cannot process finished tournament (tournament ID = " + id + ")")
            PreconditionFailed(views.html.error("Tournament is already finished."))
          }
        } catch {
          case e: Exception =>
            log.error("Error occurred while processing tournament: " + e)
            InternalServerError(views.html.error("Error occurred while processing tournament: " + e))
        }
      }
    }
  }
}