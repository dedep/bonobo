package models.core.tournament

import models.core.Common
import Common._
import models.core.round.Round
import models.core.round.group.GroupRound
import models.core.round.pair.PlayoffRound
import models.core.team.Team
import models.core.utils.MathUtils

class TournamentImpl(override val teams: List[Team], roundsCbn: => List[Round] = Nil) extends Tournament {
  require(teams.size >= 2)

  override lazy val rounds = roundsCbn

  def doStep(): Tournament = {
    val lastRound: Option[Round] = rounds.headOption

    if (lastRound.isEmpty) createFirstRound()
    else if (lastRound.get.isFinished()) {
      lastRound.get match {
        case round: PlayoffRound =>
          if (round.preliminary)
            createNextRound(teams.diff(lastRound.get.teams) ++ lastRound.get.getPromotedTeams)
          else
            createNextRound(lastRound.get.getPromotedTeams)
        case _ => createNextRound(lastRound.get.getPromotedTeams)
      }
    }
    else new TournamentImpl(teams, rounds.updated(rounds.indexOf(lastRound), lastRound.get.doStep()))
  }

  private def createFirstRound(): Tournament =
    if (isPreliminaryRoundRequired)
      new TournamentImpl(teams, new PlayoffRound(getPreliminaryRoundTeams, Nil, Nil, 0, true) :: rounds)
    else
      createNextRound(teams)

  private def createNextRound(teams: List[Team]): Tournament =
    if (teams.length >= 32)
      new TournamentImpl(teams, new GroupRound(teams) :: rounds)
    else
      new TournamentImpl(teams, new PlayoffRound(teams) :: rounds)

  private def isPreliminaryRoundRequired: Boolean = !MathUtils.isPowerOfTwo(teams.size)

  private def getPreliminaryRoundTeams: List[Team] = teams.sortBy(_.rankPoints).take(getPreliminaryRoundTeamsNumber)

  private def getPreliminaryRoundTeamsNumber: Int = (teams.length - MathUtils.getFloorPowerOfTwoNumber(teams.length)) * 2

  override def isFinished(): Boolean = rounds.headOption match {
    case None => false
    case Some(r: Round) => r.isFinished() && r.isFinalRound()
  }
}
