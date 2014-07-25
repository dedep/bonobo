package models.dedep.bonobo.core._match.evaluator

import models.dedep.bonobo.core._match.Match
import models.dedep.bonobo.core._match.result.MatchResult

trait MatchEvaluator {
  def eval(m: Match): MatchResult
}
