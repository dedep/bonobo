package models.dedep.bonobo.core._match

import models.dedep.bonobo.core._match.evaluator.MatchEvaluator
import models.dedep.bonobo.core._match.result.MatchResult
import models.dedep.bonobo.core.team.Team

case class PlayedMatch(override val aTeam: Team, override val bTeam: Team, result: MatchResult)
  extends Match(aTeam, bTeam) {

  override def eval(implicit matchEvaluator: MatchEvaluator): MatchResult =
    throw new IllegalStateException("Cannot eval played match")
}