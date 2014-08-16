package models.db_model

import models.core.tournament.TournamentImpl
import models.table.TournamentsTable
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.mvc.AnyContent

object Tournament {
  val ds = TableQuery[TournamentsTable]
  val autoIncInsert = ds.map(e => ()) returning ds.map(_.id)

  def fromId(id: Long)(implicit rs: DBSessionRequest[AnyContent]): Option[models.core.tournament.Tournament] =
    (for (tournament <- Tournament.ds if tournament.id === id) yield tournament).firstOption match {
      case None => None
      case _    => Some(
        new TournamentImpl(
          CitiesTournaments.ds.filter(_.tournamentId === id).list.map(a => City.fromId(a._2)
            .getOrElse(throw new IllegalStateException())),

          Round.ds.filter(_.tournamentId === id).list().map(a => Round.fromId(a._5)
            .getOrElse(throw new IllegalStateException()))
        )
      )
    }
}
