package models._match

import com.typesafe.scalalogging.slf4j.Logger
import models.Common
import models._match.evaluator.MatchEvaluator
import models._match.result.MatchResult
import models.team.Team
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import Common._

class Match(val aTeam: Team, val bTeam: Team, val id: Option[Long] = None) {

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  val playDate: Option[DateTime] = None

  lazy val winProbability: Long  = Math.round(matchEvaluator.calculateWinProbability(this) * 100)
  lazy val drawProbability: Long = Math.round(matchEvaluator.calculateDrawProbability(this) * 100)
  lazy val loseProbability: Long = Math.round(matchEvaluator.calculateLoseProbability(this) * 100)

  def eval(implicit matchEvaluator: MatchEvaluator): MatchResult = matchEvaluator.eval(this)

  override def toString = aTeam.toString + " vs " + bTeam.toString
}

object Match {
  def apply(aTeam: Team, bTeam: Team, id: Option[Long] = None) = new Match(aTeam, bTeam, id)
}