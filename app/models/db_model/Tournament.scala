package models.db_model

import models.core.round.Round
import models.core.team.Team
import models.core.tournament.{Tournament, TournamentImpl}
import models.table.{CitiesTournamentsTable, TournamentsTable}
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick._
import play.api.mvc.AnyContent

import scala.slick.jdbc.JdbcBackend

object Tournament {
  val ds = TableQuery[TournamentsTable]
  val citiesDs = TableQuery[CitiesTournamentsTable]

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models.core.tournament.Tournament] =
    (for (tournament <- Tournament.ds if tournament.id === id) yield tournament).firstOption match {
      case None => None
      case Some((name: String)) => Some(
        new TournamentImpl(getTournamentCities(id), name, getTournamentRounds(id), Some(id)))
    }

  private def getTournamentCities(id: Long)(implicit rs: JdbcBackend#Session): List[Team] =
    citiesDs.filter(_.tournamentId === id).list.map(a => City.fromId(a._1).getOrElse(throw new IllegalStateException()))

  private def getTournamentRounds(id: Long)(implicit rs: JdbcBackend#Session): List[Round] =
    Round.ds.filter(_.tournamentId === id).map(_.id).list.map(Round.fromId(_).getOrElse(throw new IllegalStateException()))

  def saveOrUpdate(t: Tournament)(implicit rs: JdbcBackend#Session): Long = {
    if (t.teams.exists(team => City.fromId(team.id).isEmpty))
      throw new IllegalStateException("Tournament cannot refer to non-existent city")

    t.teams.foreach(team => City.saveOrUpdate(team.asInstanceOf[City]))
    val index = if (t.id.nonEmpty && Tournament.fromId(t.id.get).nonEmpty) update(t) else save(t)
    if (t.rounds.nonEmpty) t.rounds.foreach(Round.saveOrUpdate(_, index))

    index
  }

  private def save(t: Tournament)(implicit rs: JdbcBackend#Session): Long = {
    val newIndex = (ds returning ds.map(_.id)) += t.name
    citiesDs.filter(_.tournamentId === newIndex).delete
    citiesDs ++= t.teams.map(city => (city.id, newIndex))

    newIndex
  }

  private def update(t: Tournament)(implicit rs: JdbcBackend#Session): Long = {
    ds.filter(_.id === t.id.get).update(t.name)
    t.teams.foreach(team => citiesDs.filter(c => c.cityId === team.id && c.tournamentId === t.id).update(team.id, t.id.get))

    t.id.get
  }
}
