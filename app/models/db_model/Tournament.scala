package models.db_model

import models.core.tournament.{Tournament, TournamentImpl}
import models.table.{CitiesTournamentsTable, TournamentsTable}
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.mvc.AnyContent

object Tournament {
  val ds = TableQuery[TournamentsTable]
  val citiesDs = TableQuery[CitiesTournamentsTable]

  def fromId(id: Long)(implicit rs: DBSessionRequest[AnyContent]): Option[models.core.tournament.Tournament] =
    (for (tournament <- Tournament.ds if tournament.id === id) yield tournament).firstOption match {
      case None => None
      case Some((name: String)) => Some(
        new TournamentImpl( //todo: do osobnych metod --- UWAGA RZEŹBA ---
          citiesDs.filter(_.tournamentId === id).list.map(a => City.fromId(a._2)
            .getOrElse(throw new IllegalStateException())),

          name,

          Round.ds.filter(_.tournamentId === id).map(_.id).list.map(Round.fromId(_)
            .getOrElse(throw new IllegalStateException())),

          Some(id)
        )
      )
    }

  //todo: wypadałoby sprawdzić przed updatem czy id istnieje
  def saveOrUpdate(t: Tournament)(implicit rs: DBSessionRequest[AnyContent]): Option[Long] = {
    try {
      val index = if (t.id.isEmpty) save(t) else update(t)
      if (t.rounds.nonEmpty) t.rounds.foreach(Round.saveOrUpdate(_, index))

      Some(index)
    } catch {
      case e: Exception => {
        //TODO: ++ log
        None
      }
    }
  }

  //todo: zwracanie None w przypadku fackupu
  private def save(t: Tournament)(implicit rs: DBSessionRequest[AnyContent]): Long = {
    val newIndex = (ds returning ds.map(_.id)) += t.name
    citiesDs ++= t.teams.map(city => (city.id, newIndex))
    newIndex
  }

  //todo: zwracanie None w przypadku fackupu
  private def update(t: Tournament)(implicit rs: DBSessionRequest[AnyContent]): Long = {
    ds.filter(_.id === t.id.get).update(t.name)
    t.teams.foreach(city => citiesDs.insertOrUpdate(city.id, t.id.get))
    t.id.get
  }
}
