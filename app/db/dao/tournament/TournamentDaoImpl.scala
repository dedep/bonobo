package db.dao.tournament

import com.typesafe.scalalogging.slf4j.Logger
import db.dao.city.CityDao
import db.dao.round.RoundDao
import db.dao.territory.TerritoryDao
import db.table._
import models.team.Team
import models.territory.Territory
import models.tournament.{Tournament, TournamentImpl, TournamentStatus}
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}
import utils.FunLogger._

import scala.slick.jdbc.JdbcBackend

class TournamentDaoImpl(implicit inj: Injector) extends TournamentDao with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  private val roundDao = inject[RoundDao]
  private val cityDao = inject[CityDao]
  private val territoryDao = inject[TerritoryDao]
  private val rulesDao = inject[TournamentRulesDao]

  private val ds = TableQuery[TournamentsTable]
  private val citiesDs = TableQuery[CitiesTournamentsTable]
  private val rulesDs = TableQuery[TournamentRulesTable]

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models.tournament.Tournament] =
    (for (tournament <- ds if tournament.id === id) yield tournament)
      .firstOption.map(t => instantiateFromTableRow(t))

  private def instantiateFromTableRow(row: TournamentDBRow)(implicit rs: JdbcBackend#Session): Tournament = {
    val tournamentCities = getTournamentCities(row.id)
    val cities = tournamentCities._1
    val playingTeams = tournamentCities._2
    val status = TournamentStatus.withName(row.status)
    val rules = rulesDao.fromTournamentId(row.id)
      .getOrElse(throw new IllegalStateException("Inconsistent DB state while looking for tournament rules in " + row.id))
    val territory = territoryDao.find(row.territoryId)
      .getOrElse(throw new IllegalStateException("Unknown territory " + row.territoryId))

    lazy val rounds = roundDao.getTournamentRounds(row.id)

    new TournamentImpl(territory, cities, row.name, rounds, Some(row.id), status, playingTeams)(rules)
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

        updateTournamentsTerritoryModifiableTrait(t)
      }.getOrElse(throw new IllegalArgumentException("Cannot update rounds in non-started tournament"))

      t.id.get
    }
  }

  private def updateTournamentsTerritoryModifiableTrait(t: Tournament)(implicit rs: JdbcBackend#Session) = {
    val modifiable = getActiveTournamentsWithinTerritory(t.territory.id)(rs).isEmpty
    val newTerritory = new Territory(t.territory.id, t.territory.code, t.territory.name, t.territory.population,
      t.territory.container, t.territory.isCountry, modifiable)

    territoryDao.update(newTerritory)(rs)
  }

  private def updateTournamentRow(t: Tournament)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.id === t.id.get)
      .update(TournamentDBRow(t.id.get, t.name, t.status.toString, t.territory.id))

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

    val newIndex = ds.map(_.autoInc) returning ds.map(_.id) += NewTournamentDBRow(t.name, t.status.toString, t.territory.id)
    citiesDs.filter(_.tournamentId === newIndex).delete
    citiesDs ++= t.teams.map(city => (city.id, newIndex.toLong, true))
    rulesDs += (newIndex, t.gameRules.winPoints, t.gameRules.drawPoints, t.gameRules.losePoints)

    newIndex
      .log(x => "After saving new tournament " + t.name).info()
  }

  override def getActiveTournaments()(implicit rs: JdbcBackend#Session): List[Tournament] =
    getActiveTournamentsWithCustomFilter(f => true)

  override def getActiveTournamentsWithinTerritory(territory: Territory)(implicit rs: JdbcBackend#Session): List[Tournament] =
    getActiveTournamentsWithCustomFilter(_.territoryId === territory.id)

  override def getActiveTournamentsWithinTerritory(territoryId: Long)(implicit rs: JdbcBackend#Session): List[Tournament] =
    getActiveTournamentsWithCustomFilter(_.territoryId === territoryId)

  private def getActiveTournamentsWithCustomFilter(filter: TournamentsTable => Column[Boolean])
                                                  (implicit rs: JdbcBackend#Session): List[Tournament] =
    ds.filter(_.status === TournamentStatus.PLAYING.toString).filter(filter)
      .list.map(instantiateFromTableRow)
}
