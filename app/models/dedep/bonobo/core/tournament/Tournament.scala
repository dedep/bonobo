package models.dedep.bonobo.core.tournament

import models.dedep.bonobo.core.round.Round
import models.dedep.bonobo.core.team.Team

trait Tournament {
  val teams: List[Team]
  val rounds: List[Round]

  def doStep(): Tournament

  def isFinished(): Boolean
}
