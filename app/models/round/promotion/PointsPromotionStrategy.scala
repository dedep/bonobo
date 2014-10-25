package models.round.promotion

import models.Common
import models.round.Round
import models.team.Team
import Common._

import scala.util.Sorting

object PointsPromotionStrategy extends PromotionsStrategy {
  override def arbitratePromotions(round: Round): List[Team] =
    round.units.map(_.results)
      .map(Sorting.stableSort(_).reverse)
      .flatMap(_.take(round.promotedTeamsCount))
      .map(_.team)
}