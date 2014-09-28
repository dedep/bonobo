package models

import models._match.Match
import models._match.evaluator.NormalDistributionBasedMatchEvaluator
import models.round.promotion.{PointsPromotionStrategy, TeamResultsOrdering}
import models.round.result.FootballPointsGrantingStrategy
import models.team.Team

object Common {
  type Pot = List[Team]
  type Group = List[Team]
  type Fixture = List[Match]

  implicit val pointsGrantingStrategy = FootballPointsGrantingStrategy
  implicit val matchEvaluator = NormalDistributionBasedMatchEvaluator
  implicit val promotionsStrategy = PointsPromotionStrategy
  implicit val teamResultsOrdering = TeamResultsOrdering
}
