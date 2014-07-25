package models.dedep.bonobo.core

import models.dedep.bonobo.core._match.Match
import models.dedep.bonobo.core._match.evaluator.NormalDistributionBasedMatchEvaluator
import models.dedep.bonobo.core.round.promotion.PointsPromotionStrategy
import models.dedep.bonobo.core.round.result.FootballPointsGrantingStrategy
import models.dedep.bonobo.core.team.Team

object Common {
  type Pot = List[Team]
  type Group = List[Team]
  type Fixture = List[Match]

  implicit val pointsGrantingStrategy = FootballPointsGrantingStrategy
  implicit val matchEvaluator = NormalDistributionBasedMatchEvaluator
  implicit val promotionsStrategy = PointsPromotionStrategy
}
