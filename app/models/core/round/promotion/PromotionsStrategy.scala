package models.core.round.promotion

import models.core.round.Round
import models.core.team.Team

trait PromotionsStrategy {
  def arbitratePromotions(unit: Round): List[Team]
}
