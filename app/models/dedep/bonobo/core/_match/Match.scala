package models.dedep.bonobo.core._match

import com.typesafe.scalalogging.slf4j.Logger
import models.dedep.bonobo.core._match.evaluator.MatchEvaluator
import models.dedep.bonobo.core._match.result.MatchResult
import models.dedep.bonobo.core.team.Team
import org.slf4j.LoggerFactory

class Match(val aTeam: Team, val bTeam: Team) {

  val log = Logger(LoggerFactory.getLogger(this.getClass))

  def eval(implicit matchEvaluator: MatchEvaluator): MatchResult = matchEvaluator.eval(this)

  override def toString = aTeam.toString + " vs " + bTeam.toString
}

object Match {
  def apply(aTeam: Team, bTeam: Team) = new Match(aTeam, bTeam)
}