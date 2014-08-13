package models.core

import models.core._match.Match
import models.core._match.evaluator.NormalDistributionBasedMatchEvaluator
import models.core.round.promotion.PointsPromotionStrategy
import models.core.round.result.FootballPointsGrantingStrategy
import models.core.team.Team

object Common {
  type Pot = List[Team]
  type Group = List[Team]
  type Fixture = List[Match]

  implicit val pointsGrantingStrategy = FootballPointsGrantingStrategy
  implicit val matchEvaluator = NormalDistributionBasedMatchEvaluator
  implicit val promotionsStrategy = PointsPromotionStrategy
}
