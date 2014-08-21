package models.db_model

import models.core.tournament.{Tournament, TournamentImpl}
import models.table.TournamentsTable
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.mvc.AnyContent

import scala.xml.XML

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
            .getOrElse(throw new IllegalStateException())),

          Some(id)
        )
      )
    }

  //todo: wywalić to saveNew na rzecz normalnego save
  //todo: zwracanie None w przypadku niepowodzenia
  def saveNew(t: Tournament)(implicit rs: DBSessionRequest[AnyContent]): Option[Long] = {
    val newIndex = Tournament.ds.foldLeft(0l){ Math.max } + 1
    Tournament.ds.forceInsert(newIndex)
    CitiesTournaments.autoIncInsert
      .insertAll(t.teams.map(city => (city.asInstanceOf[City].id, newIndex)):_*)

    Some(newIndex)
  }

  def update(t: Tournament)(implicit rs: DBSessionRequest[AnyContent]): Option[Long] = {
    ds.where(_.id === t.id.get).update(t.id.get)

    if (t.rounds.nonEmpty) t.rounds.foreach(Round.saveOrUpdate)
    //todo: update rund itd...

    Some(t.id.get)
  }
}
