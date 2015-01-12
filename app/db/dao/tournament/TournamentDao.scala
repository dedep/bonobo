package db.dao.tournament

import models.territory.Territory
import models.tournament.Tournament

import scala.slick.jdbc.JdbcBackend

trait TournamentDao {
  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models.tournament.Tournament]

  def updateLastRound(t: Tournament)(implicit rs: JdbcBackend#Session): Long

  def saveNew(t: Tournament)(implicit rs: JdbcBackend#Session): Long

  def getActiveTournaments()(implicit rs: JdbcBackend#Session): List[Tournament]

  def getActiveTournamentsWithinTerritory(territory: Territory)(implicit rs: JdbcBackend#Session): List[Tournament]
}