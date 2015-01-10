package models._match

import com.typesafe.scalalogging.slf4j.Logger
import models._match.result.MatchResult
import models.reverse.RoundUnitInfo
import models.team.Team
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import scaldi.{Injectable, Injector}
import service.game.evaluator.MatchEvaluator

class Match(val aTeam: Team, val bTeam: Team, val id: Option[Long] = None)
           (val unitInfo: RoundUnitInfo)
           (implicit inj: Injector) extends Injectable {

  private val log = Logger(LoggerFactory.getLogger("app"))

  private val matchEvaluator = inject[MatchEvaluator]

  val playDate: Option[DateTime] = None

  lazy val winProbability: Long  = Math.round(matchEvaluator.calculateWinProbability(this)  * 100)
  lazy val drawProbability: Long = Math.round(matchEvaluator.calculateDrawProbability(this) * 100)
  lazy val loseProbability: Long = Math.round(matchEvaluator.calculateLoseProbability(this) * 100)

  def eval(): MatchResult = matchEvaluator.eval(this)

  override def toString = aTeam.toString + " vs " + bTeam.toString
}