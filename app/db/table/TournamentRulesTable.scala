package db.table

import play.api.db.slick.Config.driver.simple._

class TournamentRulesTable(tag: Tag) extends Table[(Long, Double, Double, Double)](tag, "tournament_rules") {
  def tournamentId = column[Long]("tournament_id", O.PrimaryKey)
  def winPoints = column[Double]("win_points", O.NotNull)
  def drawPoints = column[Double]("draw_points", O.NotNull)
  def losePoints = column[Double]("lose_points", O.NotNull)

  def * = (tournamentId, winPoints, drawPoints, losePoints)
}
