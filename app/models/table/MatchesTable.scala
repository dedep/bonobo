package models.table

import org.joda.time.DateTime
import play.api.db.slick.Config.driver.simple._
import com.github.tototoshi.slick.PostgresJodaSupport._

class MatchesTable(tag: Tag) extends Table[(Long, Int, Long, Option[Int], Long, Option[Int], Option[DateTime])](tag, "matches") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def unitId = column[Long]("unit_id", O.NotNull)
  def fixtureNum = column[Int]("fixture", O.NotNull)
  def aTeamId = column[Long]("a_team_id", O.NotNull)
  def aTeamGoals = column[Option[Int]]("a_team_goals", O.Nullable)
  def bTeamId = column[Long]("b_team_id", O.NotNull)
  def bTeamGoals = column[Option[Int]]("b_team_goals", O.Nullable)
  def playDate = column[Option[DateTime]]("play_date", O.NotNull)

  def * = (unitId, fixtureNum, aTeamId, aTeamGoals, bTeamId, bTeamGoals, playDate)
}
