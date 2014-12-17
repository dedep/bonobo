package db_access.dao.unit

import models.round.RoundUnit
import models.team.Team
import scala.slick.jdbc.JdbcBackend

trait UnitDao {
  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[RoundUnit]

  def saveOrUpdate(u: RoundUnit, parentRoundId: Long, fixturesToUpdate: Set[Int])(implicit rs: JdbcBackend#Session): Long

  def getAllWithinRound(roundId: Long)(implicit rs: JdbcBackend#Session): List[RoundUnit]

  def getPromotedTeamsWithinRound(roundId: Long)(implicit rs: JdbcBackend#Session): List[Team]
}