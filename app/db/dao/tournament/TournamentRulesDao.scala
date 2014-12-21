package db.dao.tournament

import models.tournament.GameRules

import scala.slick.jdbc.JdbcBackend

trait TournamentRulesDao {
  def fromTournamentId(id: Long)(implicit rs: JdbcBackend#Session): Option[GameRules]
}
