package models.dedep.bonobo.core.round.promotion

import models.dedep.bonobo.core.round.Round
import models.dedep.bonobo.core.team.Team

trait PromotionsStrategy {
  def arbitratePromotions(unit: Round): List[Team]
}
