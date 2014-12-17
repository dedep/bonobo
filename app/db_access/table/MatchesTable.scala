package db_access.table

import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import com.github.tototoshi.slick.PostgresJodaSupport._

case class MatchDBRow(unitId: Long, fixtureNum: Int, aTeamId: Long, aTeamGoals: Option[Int], bTeamId: Long, bTeamGoals: Option[Int], playDate: Option[DateTime])

class MatchesTable(tag: Tag) extends Table[MatchDBRow](tag, "matches") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def unitId = column[Long]("unit_id", O.NotNull)
  def fixtureNum = column[Int]("fixture", O.NotNull)
  def aTeamId = column[Long]("a_team_id", O.NotNull)
  def aTeamGoals = column[Option[Int]]("a_team_goals", O.Nullable)
  def bTeamId = column[Long]("b_team_id", O.NotNull)
  def bTeamGoals = column[Option[Int]]("b_team_goals", O.Nullable)
  def playDate = column[Option[DateTime]]("play_date", O.NotNull)

  def * = (unitId, fixtureNum, aTeamId, aTeamGoals, bTeamId, bTeamGoals, playDate) <> (MatchDBRow.tupled, MatchDBRow.unapply)
}
