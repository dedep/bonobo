package models.round.result

import models._match.result.{Draw, MatchResult, WinA, WinB}
import models.team.Team

//todo: test wygranych, przegranych, zremisowanych ...
case class TeamResult(team: Team, points: Double = 0, goalsScored: Int = 0, goalsConceded: Int = 0,
                       wins: Int = 0, draws: Int = 0, loses: Int = 0) {
  def aPlus(mResult: MatchResult)(implicit pointsGrantingStrategy: PointsGrantingStrategy): TeamResult = mResult match {
    case r: WinA =>
      TeamResult(team, points + pointsGrantingStrategy.POINTS_FOR_WIN, goalsScored + r.aGoals, goalsConceded + r.bGoals,
      wins + 1, draws, loses)
    case r: WinB =>
      TeamResult(team, points + pointsGrantingStrategy.POINTS_FOR_LOSE, goalsScored + r.aGoals, goalsConceded + r.bGoals,
      wins, draws, loses + 1)
    case r: Draw =>
      TeamResult(team, points + pointsGrantingStrategy.POINTS_FOR_DRAW, goalsScored + r.aGoals, goalsConceded + r.bGoals,
      wins, draws + 1, loses)
  }

  def bPlus(mResult: MatchResult)(implicit pointsGrantingStrategy: PointsGrantingStrategy): TeamResult = mResult match {
    case r: WinA =>
      TeamResult(team, points + pointsGrantingStrategy.POINTS_FOR_LOSE, goalsScored + r.bGoals, goalsConceded + r.aGoals,
      wins, draws, loses + 1)
    case r: WinB =>
      TeamResult(team, points + pointsGrantingStrategy.POINTS_FOR_WIN, goalsScored + r.bGoals, goalsConceded + r.aGoals,
      wins + 1, draws, loses)
    case r: Draw =>
      TeamResult(team, points + pointsGrantingStrategy.POINTS_FOR_DRAW, goalsScored + r.bGoals, goalsConceded + r.aGoals,
      wins, draws + 1, loses)
  }

  lazy val matchesAmount = wins + draws + loses
}