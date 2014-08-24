package models.table

import play.api.db.slick.Config.driver.simple._

class MatchesTable(tag: Tag) extends Table[(Long, Int, Long, Option[Int], Long, Option[Int])](tag, "matches") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def unitId = column[Long]("unit_id", O.NotNull)
  def fixtureNum = column[Int]("fixture", O.NotNull)
  def aTeamId = column[Long]("a_team_id", O.NotNull)
  def aTeamGoals = column[Option[Int]]("a_team_goals", O.Nullable)
  def bTeamId = column[Long]("b_team_id", O.NotNull)
  def bTeamGoals = column[Option[Int]]("b_team_goals", O.Nullable)

  def * = (unitId, fixtureNum, aTeamId, aTeamGoals, bTeamId, bTeamGoals)
}
