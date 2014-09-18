package models.core.round.promotion

import models.core.round.Round
import models.core.team.Team
import models.core.Common._

import scala.util.Sorting

object PointsPromotionStrategy extends PromotionsStrategy {
  override def arbitratePromotions(round: Round): List[Team] = {
    round.units.map(_.results)
      .map(Sorting.stableSort(_).reverse)
      .flatMap(_.take(round.getPromotedTeamsCount))
      .map(_.team)
  }
}