package controllers

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc._
import play.api.Play.current
import scala.slick.lifted.TableQuery
import models.{City, Territory, TerritoriesTable}
import models.dedep.bonobo.core.tournament.TournamentImpl

object TerritoryController extends Controller {
  val territories = TableQuery[TerritoriesTable]

  def find(id: Long) = DBAction { implicit rs =>
    (for (territory <- territories if territory.id === id) yield territory).firstOption match {
      case None => NotFound("Territory not found")
      case Some(t: Territory) => Ok(views.html.territory(t))
    }
  }

  def startTournament(id: Long) = DBAction { implicit rs =>
    (for (territory <- territories ; city <- CityController.cities if territory.id === id && city.territoryId === territory.id) yield (territory, city)).list match {
      case Nil => NotFound("Territory not found")
      case l: List[(Territory, City)] => {
        if (l.size < 2)
          PreconditionFailed("Tournament requires at least 2 cities in territory")
        else {
          val tournament = TournamentImpl(l.map(_._2))
          Ok(tournament.teams.map(_.name).mkString(", "))
        }
      }
    }
  }

}
