package models.core._match.evaluator

import models.core._match.Match
import models.core._match.result.MatchResult

trait MatchEvaluator {
  def eval(m: Match): MatchResult
}
