package models.round.promotion

import models.round.{RoundUnit, Round}
import models.team.Team

trait PromotionsStrategy {
  def findPromotedAndEliminatedTeams(unit: RoundUnit): (List[Team], List[Team])
}
