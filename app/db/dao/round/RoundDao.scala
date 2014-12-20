package db.dao.round

import models.round.Round

import scala.slick.jdbc.JdbcBackend

trait RoundDao {
  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models.round.Round]

  def saveOrUpdate(r: Round, parentTournamentId: Long)(implicit rs: JdbcBackend#Session): Long

  def getTournamentRounds(tournamentId: Long)(implicit rs: JdbcBackend#Session): List[Round]
}