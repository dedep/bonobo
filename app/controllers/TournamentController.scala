package controllers

import models.core.tournament.Tournament
import models.core.tournament.Tournament
import models.db_model.Tournament
import play.api.db.slick._
import play.api.mvc._

object TournamentController extends Controller {
  def find(id: Long) = DBAction { implicit rs =>
    Tournament.fromId(id) match {
      case None => NotFound("Tournament not found")
      case Some(t: (Tournament)) => Ok(t.teams.map(_.name).mkString("Cities: [", ", ", "]"))
    }
  }
}