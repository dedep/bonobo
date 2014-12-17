package models.tournament

import models.Common
import Common._
import models.round.{RoundStatus, Round}
import models.round.group.GroupRound
import models.round.pair.PlayoffRound
import models.team.Team
import models.tournament.TournamentStatus.TournamentStatus
import utils.MathUtils

class TournamentImpl(override val teams: List[Team], override val name: String, roundsCbn: => List[Round] = Nil,
                     override val id: Option[Long] = None, override val status: TournamentStatus = TournamentStatus.NOT_STARTED,
                     val playingTeams: List[Boolean] = Nil) extends Tournament {

  require(teams.size >= 2)

  override lazy val rounds = roundsCbn

  override val teamsInGame: List[Boolean] = if (playingTeams.isEmpty) teams.map(t => true) else playingTeams

  override def doStep(): Tournament = {
    val lastRound: Option[Round] = rounds.headOption

    if (lastRound.isEmpty) createFirstRound()
    else if (lastRound.get.isFinished) {
      lastRound.get match {
        case round: PlayoffRound =>
          if (round.isPreliminary) {
            createNextRound(teams.diff(lastRound.get.teams) ++ lastRound.get.getPromotedTeams)
          }
          else {
            createNextRound(lastRound.get.getPromotedTeams)
          }
        case _ => createNextRound(lastRound.get.getPromotedTeams)
      }
    }
    else processLastRound()
  }

  private def createFirstRound(): Tournament =
    if (isPreliminaryRoundRequired) {
      val roundName = "Preliminary round"
      val teams = getPreliminaryRoundTeams
      new TournamentImpl(this.teams, name, new PlayoffRound(roundName, teams, Nil, Nil, 0, true) :: rounds,
        id, TournamentStatus.PLAYING, this.teams.map(x => true))
    }
    else {
      createNextRound(teams)
    }

  private def createNextRound(teams: List[Team]): Tournament = {
    val roundIndex = if (isPreliminaryRoundRequired) rounds.size else rounds.size + 1

    if (teams.length >= 32) {
      val roundName = "Round " + roundIndex
      val newRounds = new GroupRound(roundName, teams) :: rounds
      new TournamentImpl(this.teams, name, newRounds, id, TournamentStatus.PLAYING, this.teams.map(t => greedyIsTeamStillInGame(t.id, newRounds)))
    }
    else {
      val roundName = "Round " + roundIndex
      val newRounds = new PlayoffRound(roundName, teams) :: rounds
      new TournamentImpl(this.teams, name, newRounds, id, TournamentStatus.PLAYING, this.teams.map(t => greedyIsTeamStillInGame(t.id, newRounds)))
    }
  }

  //todo: testy jednostkowe statusów
  private def processLastRound(): Tournament = {
    val lastRound: Option[Round] = rounds.headOption
    val newRounds = rounds.updated(rounds.indexOf(lastRound), lastRound.get.doStep())
    val status = {
      val headRound = newRounds.head
      if (headRound.isFinished && headRound.isFinalRound) TournamentStatus.FINISHED
      else TournamentStatus.PLAYING
    }

    new TournamentImpl(this.teams, name, newRounds, id, status, this.teams.map(t => greedyIsTeamStillInGame(t.id, newRounds)))
  }

  private def greedyIsTeamStillInGame(teamId: Long, rounds: List[Round]): Boolean = {
    rounds.isEmpty || rounds.head.teams.exists(_.id == teamId) || rounds.head.isPreliminary
  }

  private def isPreliminaryRoundRequired: Boolean = !MathUtils.isPowerOfTwo(teams.size)

  private def getPreliminaryRoundTeams: List[Team] = teams.sortBy(_.rankPoints).take(getPreliminaryRoundTeamsNumber)

  private def getPreliminaryRoundTeamsNumber: Int = (teams.length - MathUtils.getFloorPowerOfTwoNumber(teams.length)) * 2
}
