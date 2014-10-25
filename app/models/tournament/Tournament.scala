package models.tournament

import models.round.Round
import models.team.Team

trait Tournament {
  val teams: List[Team]
  val rounds: List[Round]
  val name: String
  val id: Option[Long]

  //todo: na razie nieużywane
  val teamsWithTheirLastRound: Map[Team, String]

  def doStep(): Tournament

  def isFinished(): Boolean

  def isTeamStillPlaying(teamId: Long): Boolean = {
    rounds.isEmpty || rounds.head.teams.exists(_.id == teamId) || rounds.head.isPreliminary
  }

  val status: TournamentStatus.TournamentStatus = {
    if (rounds.isEmpty) TournamentStatus.NOT_STARTED
    else if (isFinished()) TournamentStatus.FINISHED
    else TournamentStatus.PLAYING
  }

  val isPlayed = status == TournamentStatus.PLAYING

  object TournamentStatus extends Enumeration {
    val NOT_STARTED, PLAYING, FINISHED = Value

    type TournamentStatus = Value
  }
}
