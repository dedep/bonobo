package models.core._match

import models.core._match.evaluator.MatchEvaluator
import models.core._match.result.MatchResult
import models.core.team.Team
import org.joda.time.DateTime

case class PlayedMatch(override val aTeam: Team, override val bTeam: Team, result: MatchResult, override val playDate: Option[DateTime],
                       override val id: Option[Long] = None)
  extends Match(aTeam, bTeam, id) {

  override def toString: String = super.toString + " " + result.toString

  override def eval(implicit matchEvaluator: MatchEvaluator): MatchResult =
    throw new IllegalStateException("Cannot eval played match")
}