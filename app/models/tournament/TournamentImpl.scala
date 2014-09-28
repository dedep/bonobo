package models.tournament

import models.Common
import Common._
import models.Common
import models.round.Round
import models.round.group.GroupRound
import models.round.pair.PlayoffRound
import models.team.Team
import utils.MathUtils

class TournamentImpl(override val teams: List[Team], override val name: String,
                     roundsCbn: => List[Round] = Nil, override val id: Option[Long] = None) extends Tournament {

  require(teams.size >= 2)

  override lazy val rounds = roundsCbn

  def doStep(): Tournament = {
    val lastRound: Option[Round] = rounds.headOption

    if (lastRound.isEmpty) createFirstRound()
    else if (lastRound.get.isFinished()) {
      lastRound.get match {
        case round: PlayoffRound =>
          if (round.preliminary) {
            createNextRound(teams.diff(lastRound.get.teams) ++ lastRound.get.getPromotedTeams)
          }
          else {
            createNextRound(lastRound.get.getPromotedTeams)
          }
        case _ => createNextRound(lastRound.get.getPromotedTeams)
      }
    }
    else new TournamentImpl(teams, name, rounds.updated(rounds.indexOf(lastRound), lastRound.get.doStep()), id)
  }

  private def createFirstRound(): Tournament =
    if (isPreliminaryRoundRequired) {
      val roundName = "Preliminary round"
      new TournamentImpl(teams, name, new PlayoffRound(roundName, getPreliminaryRoundTeams, Nil, Nil, 0, true) :: rounds, id)
    }
    else {
      createNextRound(teams)
    }

  private def createNextRound(teams: List[Team]): Tournament = {
    val roundIndex = if (isPreliminaryRoundRequired) rounds.size else rounds.size + 1

    if (teams.length >= 32) {
      val roundName = "Round " + roundIndex
      new TournamentImpl(teams, name, new GroupRound(roundName, teams) :: rounds, id)
    }
    else {
      val roundName = "Round " + roundIndex
      new TournamentImpl(teams, name, new PlayoffRound(roundName, teams) :: rounds, id)
    }
  }

  private def isPreliminaryRoundRequired: Boolean = !MathUtils.isPowerOfTwo(teams.size)

  private def getPreliminaryRoundTeams: List[Team] = teams.sortBy(_.rankPoints).take(getPreliminaryRoundTeamsNumber)

  private def getPreliminaryRoundTeamsNumber: Int = (teams.length - MathUtils.getFloorPowerOfTwoNumber(teams.length)) * 2

  override def isFinished(): Boolean = rounds.headOption match {
    case None => false
    case Some(r: Round) => r.isFinished() && r.isFinalRound()
  }

  //todo: TEST !!!!! TODO: NIEDZIELA
  lazy val teamsWithTheirLastRound: Map[Team, String] = {
    def updateTeamRoundPairs(rs: List[Round])(acc: List[(Team, String)]): List[(Team, String)] = {
      rs.headOption match {
        case None => acc
        case Some(r) =>
          val prevRoundsAcc = acc.filterNot(r.teams.contains(_))
          updateTeamRoundPairs(rs.tail)(prevRoundsAcc ::: r.teams.map((_, r.name)))
      }
    }

    updateTeamRoundPairs(rounds.reverse)(Nil).toMap
  }
}
