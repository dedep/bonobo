package db_access.dao._match

import models.Common.Fixture
import scala.slick.jdbc.JdbcBackend

trait MatchDao {

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models._match.Match]

  def fromId(ids: Seq[Long])(implicit rs: JdbcBackend#Session): List[models._match.Match]

  def saveOrUpdate(m: models._match.Match, fixtureNum: Int, unitId: Long)(implicit rs: JdbcBackend#Session): Long

  def getFixturesWithinUnit(id: Long)(implicit rs: JdbcBackend#Session): List[Fixture]
}
