package models.tournament

import models.round.{GroupRound, PlayoffRound, Round}
import models.territory.{City, Territory}
import models.tournament.TournamentStatus.TournamentStatus
import scaldi.Injector
import utils.MathUtils

class TournamentImpl(override val territory: Territory, override val teams: List[City], override val name: String,
                     roundsCbn: => List[Round] = Nil, override val id: Option[Long] = None,
                     override val status: TournamentStatus = TournamentStatus.NOT_STARTED, val playingTeams: List[Boolean] = Nil)
                    (override val gameRules: GameRules)
                    (implicit inj: Injector) extends Tournament {

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
      new TournamentImpl(this.territory, this.teams, name, new PlayoffRound(roundName, teams, Nil, Nil, 0, true)(toTournamentInfo) :: rounds,
        id, TournamentStatus.PLAYING, this.teams.map(x => true))(gameRules)
    }
    else {
      createNextRound(teams)
    }

  private def createNextRound(teams: List[City]): Tournament = {
    val roundIndex = if (isPreliminaryRoundRequired) rounds.size else rounds.size + 1

    //todo: przenieść nazywanie rund to osobnego serwisu
    if (teams.length >= 32) {
      val roundName = "Round " + roundIndex
      val newRounds = new GroupRound(roundName, teams)(toTournamentInfo) :: rounds
      new TournamentImpl(this.territory, this.teams, name, newRounds, id, TournamentStatus.PLAYING,
        this.teams.map(t => greedyIsTeamStillInGame(t.id.get, newRounds)))(gameRules)
    }
    else {
      val roundName = "Round " + roundIndex
      val newRounds = new PlayoffRound(roundName, teams)(toTournamentInfo) :: rounds
      new TournamentImpl(this.territory, this.teams, name, newRounds, id, TournamentStatus.PLAYING,
        this.teams.map(t => greedyIsTeamStillInGame(t.id.get, newRounds)))(gameRules)
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

    new TournamentImpl(territory, this.teams, name, newRounds, id, status,
      this.teams.map(t => greedyIsTeamStillInGame(t.id.get, newRounds)))(gameRules)
  }

  private def greedyIsTeamStillInGame(teamId: Long, rounds: List[Round]): Boolean = {
    rounds.isEmpty || rounds.head.teams.exists(_.id == teamId) || rounds.head.isPreliminary
  }

  private def isPreliminaryRoundRequired: Boolean = !MathUtils.isPowerOfTwo(teams.size)

  private def getPreliminaryRoundTeams: List[City] = teams.sortBy(_.points).take(getPreliminaryRoundTeamsNumber)

  private def getPreliminaryRoundTeamsNumber: Int = (teams.length - MathUtils.getFloorPowerOfTwoNumber(teams.length)) * 2
}
