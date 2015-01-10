package service.game.evaluator

import com.typesafe.scalalogging.slf4j.Logger
import models._match.Match
import models._match.result.{Draw, MatchResult, WinA, WinB}
import org.apache.commons.math3.distribution.NormalDistribution
import org.slf4j.LoggerFactory

import scala.util.Random

class NormalDistributionBasedMatchEvaluator extends MatchEvaluator {

  private val log = Logger(LoggerFactory.getLogger("app"))

  private val hostPremium = 0.15

  override def eval(m: Match): MatchResult = {
    require(m.bTeam.value > 0 && m.aTeam.value > 0)
    log.info("Evaluating result of the following match: {}", m)

    val bp = calcBalancePoint(m)
    log.debug("Balance point for match: {} equals: {}", m, bp.underlying())

    val mp = drawMatchPoint(bp)
    log.debug("Match point for match: {} equals: {}", m, mp.underlying())

    val result = createResultForMatchPoint(mp)
    log.info("Match: {} result has been evaluated: {}", m, result)

    result
  }

  def calcBalancePoint(m: Match): Double =
    (log2(m.aTeam.value.toDouble / m.bTeam.value.toDouble) / 2) + hostPremium

  private def log2(x: Double) = Math.log(x) / Math.log(2)

  def drawMatchPoint(balancePoint: Double): Double = Random.nextGaussian() + balancePoint

  def createResultForMatchPoint(matchPoint: Double): MatchResult = {
    val c = drawGoalsConstantComponent
    log.debug("Goals constant equals: {}", c.underlying())

    val d = calcGoalsDiff(matchPoint)
    log.debug("Goals difference equals: {}", d.underlying())

    if (d > 0) WinA(d + c, c)
    else if (d < 0) WinB(c, c - d)
    else Draw(c)
  }

  def drawGoalsConstantComponent: Int = Math.round(Math.abs(Random.nextGaussian())).toInt

  def calcGoalsDiff(matchPoint: Double): Int =
    if (matchPoint >= 0) Math.floor(3 * matchPoint).toInt
    else -Math.floor(3 * -matchPoint).toInt

  override def calculateLoseProbability(m: Match): Double =
    new NormalDistribution(calcBalancePoint(m), 1).cumulativeProbability(-0.3333333)

  override def calculateDrawProbability(m: Match): Double =
    new NormalDistribution(calcBalancePoint(m), 1).probability(-0.3333333, 0.3333333)

  override def calculateWinProbability(m: Match): Double =
    1 - new NormalDistribution(calcBalancePoint(m), 1).cumulativeProbability(0.3333333)
}