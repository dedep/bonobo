package db.dao.tournament

import com.typesafe.scalalogging.slf4j.Logger
import db.dao.city.CityDao
import db.dao.round.RoundDao
import db.table.{TournamentRulesTable, CitiesTournamentsTable, TournamentsTable}
import models.team.Team
import models.tournament.{Tournament, TournamentImpl, TournamentStatus}
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}
import utils.FunLogger._

import scala.slick.jdbc.JdbcBackend

class TournamentDaoImpl(implicit inj: Injector) extends TournamentDao with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger(this.getClass))

  private val roundDao = inject[RoundDao]
  private val cityDao = inject[CityDao]
  private val rulesDao = inject[TournamentRulesDao]

  private val ds = TableQuery[TournamentsTable]
  private val citiesDs = TableQuery[CitiesTournamentsTable]
  private val rulesDs = TableQuery[TournamentRulesTable]

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models.tournament.Tournament] = {
    (for (tournament <- ds if tournament.id === id) yield tournament).firstOption match {
      case None => None
      case Some((name: String, status: String)) => Some(instantiateFromTableRow(id, name, status))
    }
  }

  private def instantiateFromTableRow(id: Long, name: String, statusStr: String)(implicit rs: JdbcBackend#Session): Tournament = {
    val tournamentCities = getTournamentCities(id)
    val cities = tournamentCities._1
    val playingTeams = tournamentCities._2
    val status = TournamentStatus.withName(statusStr)
    val rules = rulesDao.fromTournamentId(id)
      .getOrElse(throw new IllegalStateException("Inconsistent DB state while looking for tournament rules in " + id))

    lazy val rounds = roundDao.getTournamentRounds(id)

    new TournamentImpl(cities, name, rounds, Some(id), status, playingTeams)(rules)
  }

  private def getTournamentCities(id: Long)(implicit rs: JdbcBackend#Session): (List[Team], List[Boolean]) = {
    val tournamentTeams = citiesDs.filter(_.tournamentId === id).map(r => (r.cityId, r.isTeamStillPlaying))
      .list
    val teams = cityDao.fromId(tournamentTeams.map(_._1))
    val plays = teams.map(_.id).map(id => tournamentTeams.find(_._1 == id).get._2)
    (teams, plays)
  }

  override def updateLastRound(t: Tournament)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Updating last round for tournament: " + t.name)

    rs.withTransaction {
      t.id.map{ id =>
        log.info("Before calling safeOrUpdate in tournament " + id)
        updateTournamentRow(t)
        if (t.rounds.nonEmpty) {
          if (t.rounds.head.id.isEmpty) updateTournamentCitiesRow(t)
          roundDao.saveOrUpdate(t.rounds.head, t.id.get)
        }
      }.getOrElse(throw new IllegalArgumentException("Cannot update rounds in non-started tournament"))

      t.id.get
    }
  }

  private def updateTournamentRow(t: Tournament)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.id === t.id.get).update(t.name, t.status.toString)

  private def updateTournamentCitiesRow(t: Tournament)(implicit rs: JdbcBackend#Session): Unit =
    t.teams.zip(t.teamsInGame).foreach { case (city, plays) =>
      citiesDs.filter(r => r.cityId === city.id && r.tournamentId === t.id.get).map(_.isTeamStillPlaying).update(plays)
    }

  override def saveNew(t: Tournament)(implicit rs: JdbcBackend#Session): Long = {
    log.info("Creating new tournament with name: " + t.name)

    rs.withTransaction {
      val index = save(t)
      index
    }
  }

  private def save(t: Tournament)(implicit rs: JdbcBackend#Session): Long = {
    "".log(x => "Before saving new tournament " + t.name).info()

    val newIndex = (ds returning ds.map(_.id)) += (t.name, t.status.toString)
    citiesDs.filter(_.tournamentId === newIndex).delete
    citiesDs ++= t.teams.map(city => (city.id, newIndex, true))
    rulesDs += (newIndex, t.gameRules.winPoints, t.gameRules.drawPoints, t.gameRules.losePoints)

    newIndex
      .log(x => "After saving new tournament " + t.name).info()
  }
}
