package db_access.dao.unit

import models.round.RoundUnit
import scala.slick.jdbc.JdbcBackend

trait UnitDao {
  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[RoundUnit]

  def saveOrUpdate(u: RoundUnit, parentRoundId: Long)(implicit rs: JdbcBackend#Session): Long

  def getAllWithinRound(roundId: Long)(implicit rs: JdbcBackend#Session): List[RoundUnit]
}
