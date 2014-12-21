package db.dao.tournament

import db.table.TournamentRulesTable
import models.tournament.GameRules
import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery
import play.api.db.slick.Config.driver.simple._

class TournamentRulesDaoImpl extends TournamentRulesDao {
  val ds = TableQuery[TournamentRulesTable]

  override def fromTournamentId(id: Long)(implicit rs: JdbcBackend#Session): Option[GameRules] =
    ds.filter(_.tournamentId === id)
      .firstOption
      .map(r => instantiateFromTableRow(r._1, r._2, r._3, r._4))

  private def instantiateFromTableRow(id: Long, winPoints: Double, drawPoints: Double, losePoints: Double)
                                     (implicit rs: JdbcBackend#Session): GameRules =
    new GameRules(losePoints, drawPoints, winPoints)
}
