package models.dedep.bonobo.core.round.result

import models.dedep.bonobo.core._match.result.{Draw, MatchResult, WinA, WinB}
import models.dedep.bonobo.core.team.Team

case class TeamResult(team: Team, points: Double = 0, goalsScored: Int = 0, goalsConceded: Int = 0) {
  def aPlus(mResult: MatchResult)(implicit pointsGrantingStrategy: PointsGrantingStrategy): TeamResult = mResult match {
    case r: WinA => TeamResult(team, points + pointsGrantingStrategy.POINTS_FOR_WIN, goalsScored + r.aGoals, goalsConceded + r.bGoals)
    case r: WinB => TeamResult(team, points + pointsGrantingStrategy.POINTS_FOR_LOSE, goalsScored + r.aGoals, goalsConceded + r.bGoals)
    case r: Draw => TeamResult(team, points + pointsGrantingStrategy.POINTS_FOR_DRAW, goalsScored + r.aGoals, goalsConceded + r.bGoals)
  }

  def bPlus(mResult: MatchResult)(implicit pointsGrantingStrategy: PointsGrantingStrategy): TeamResult = mResult match {
    case r: WinA => TeamResult(team, points + pointsGrantingStrategy.POINTS_FOR_LOSE, goalsScored + r.bGoals, goalsConceded + r.aGoals)
    case r: WinB => TeamResult(team, points + pointsGrantingStrategy.POINTS_FOR_WIN, goalsScored + r.bGoals, goalsConceded + r.aGoals)
    case r: Draw => TeamResult(team, points + pointsGrantingStrategy.POINTS_FOR_DRAW, goalsScored + r.bGoals, goalsConceded + r.aGoals)
  }
}