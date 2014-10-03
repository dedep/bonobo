package db_access.dao.tournament

import com.typesafe.scalalogging.slf4j.Logger
import db_access.dao.city.CityDao
import db_access.dao.round.RoundDao
import db_access.table.{CitiesTournamentsTable, TournamentsTable}
import models.round.Round
import models.team.Team
import models.territory.City
import models.tournament.{Tournament, TournamentImpl}
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend

class TournamentDaoImpl(implicit inj: Injector) extends TournamentDao with Injectable {
  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  private val roundDao = inject[RoundDao]
  private val cityDao = inject[CityDao]

  private val ds = TableQuery[TournamentsTable]
  val citiesDs = TableQuery[CitiesTournamentsTable]

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models.tournament.Tournament] =
    (for (tournament <- ds if tournament.id === id) yield tournament).firstOption match {
      case None => None
      case Some((name: String)) => Some(
        new TournamentImpl(getTournamentCities(id), name, roundDao.getTournamentRounds(id), Some(id)))
    }

  private def getTournamentCities(id: Long)(implicit rs: JdbcBackend#Session): List[Team] =
    citiesDs.filter(_.tournamentId === id).list.map(a => cityDao.fromId(a._1).getOrElse(throw new IllegalStateException()))

  override def saveOrUpdate(t: Tournament)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Tournament saveOrUpdate call for tournament: " + t.name)

    rs.withTransaction {
      println("Trying to get team " + t.teams.map(_.id).mkString(", "))
      if (t.teams.exists(team => cityDao.fromId(team.id).isEmpty))
        throw new IllegalStateException("Tournament cannot refer to non-existent city")

      log.info("Before cities updating in following tournament: " + t.name)
      t.teams.foreach(team => cityDao.saveOrUpdate(team.asInstanceOf[City]))
      log.info("After cities updating in following tournament: " + t.name)

      val index = if (t.id.nonEmpty && fromId(t.id.get).nonEmpty) update(t) else save(t)

      if (t.rounds.nonEmpty) roundDao.saveOrUpdate(t.rounds.head, index)

      index
    }
  }

  private def save(t: Tournament)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Before saving new tournament " + t.name)

    val newIndex = (ds returning ds.map(_.id)) += t.name
    citiesDs.filter(_.tournamentId === newIndex).delete
    citiesDs ++= t.teams.map(city => (city.id, newIndex))

    log.info("After saving new tournament " + t.name)
    newIndex
  }

  private def update(t: Tournament)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Before Updating tournament " + t)

    ds.filter(_.id === t.id.get).update(t.name)
    t.teams.foreach(team => citiesDs.filter(c => c.cityId === team.id && c.tournamentId === t.id).update(team.id, t.id.get))

    log.info("After Updating tournament " + t)
    t.id.get
  }
}
