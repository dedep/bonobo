package service.game.promoter

import models.team.Team
import models.unit.RoundUnit

trait PromotionsStrategy {
  def findPromotedAndEliminatedTeams(unit: RoundUnit): (List[Team], List[Team])
}
