package models.round.promotion

import models.round.Round
import models.team.Team

trait PromotionsStrategy {
  def arbitratePromotions(unit: Round): List[Team]
}
