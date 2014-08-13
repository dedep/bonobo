package models.core.tournament

import models.core.round.Round
import models.core.team.Team

trait Tournament {
  val teams: List[Team]
  val rounds: List[Round]

  def doStep(): Tournament

  def isFinished(): Boolean
}
