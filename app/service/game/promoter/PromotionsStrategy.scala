package service.game.promoter

import models.team.Team
import models.tournament.GameRules
import models.unit.RoundUnit

trait PromotionsStrategy {
  def findPromotedAndEliminatedTeams(unit: RoundUnit)(implicit gm: GameRules): (List[Team], List[Team])
}
