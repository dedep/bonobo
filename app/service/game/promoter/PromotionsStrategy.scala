package service.game.promoter

import models.territory.City
import models.tournament.GameRules
import models.unit.RoundUnit

trait PromotionsStrategy {
  def findPromotedAndEliminatedTeams(unit: RoundUnit)(implicit gm: GameRules): (List[City], List[City])
}
