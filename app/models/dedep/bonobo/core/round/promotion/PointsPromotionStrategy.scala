package models.dedep.bonobo.core.round.promotion

import models.dedep.bonobo.core.round.Round
import models.dedep.bonobo.core.team.Team

import scala.util.Random

object PointsPromotionStrategy extends PromotionsStrategy {
  override def arbitratePromotions(round: Round): List[Team] = {
    round.units
      .map(ru => Random.shuffle(ru.results))
      .map(_
      .sortBy(r => r.goalsConceded - r.goalsScored)
      .sortBy(-_.points)
      )
      .flatMap(_.take(round.getPromotedTeamsCount))
      .map(_.team)
  }
}