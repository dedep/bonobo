package models.tournament

import models.reverse.TournamentInfo
import models.round.Round
import models.territory.{City, Territory}
import models.tournament.TournamentStatus.TournamentStatus

trait Tournament {
  val teams: List[City]
  val rounds: List[Round]
  val name: String
  val id: Option[Long]
  val status: TournamentStatus
  val teamsInGame: List[Boolean]
  val gameRules: GameRules
  val territory: Territory

  def doStep(): Tournament

  def isTeamStillInGame(teamId: Long): Boolean =
    teams.zip(teamsInGame).find(_._1.id == teamId).exists(_._2)

  lazy val teamsInGameSum = teamsInGame.count(_ == true)

  val isPlayed = status == TournamentStatus.PLAYING

  val isFinished = status == TournamentStatus.FINISHED

  protected val toTournamentInfo = new TournamentInfo(name, id, gameRules)
}
