package models.dedep.bonobo.core._match.evaluator

import com.typesafe.scalalogging.slf4j.Logger
import models.dedep.bonobo.core._match.Match
import models.dedep.bonobo.core._match.result.{Draw, MatchResult, WinA, WinB}
import org.slf4j.LoggerFactory

import scala.util.Random

object FiniteSampleMatchEvaluator extends MatchEvaluator {
  val goalConstantFactor = 1.8
  val randomSample = 60
  val goalsDifferenceFactor = 0.07968426076
  // = 1 / 12.5495297376
  val goalsDifferencePowerFactor = 1.3

  val log = Logger(LoggerFactory.getLogger(FiniteSampleMatchEvaluator.getClass))

  override def eval(m: Match): MatchResult = {
    log.info("Evaluating match: {} result", m)

    val balancePoint = calcBalancePoint(m)
    log.debug("Balance point equals {}", balancePoint.underlying())

    val matchPoint = drawPoint
    log.debug("Match point has been draw: {}", matchPoint.underlying())

    drawResult(balancePoint, matchPoint)
  }

  def calcBalancePoint(m: Match) = (m.aTeam.value / (m.aTeam.value + m.bTeam.value)) * randomSample

  def drawPoint = Random.nextDouble() * randomSample

  def drawResult(balancePoint: Double, matchPoint: Double): MatchResult = {
    val goalsDifference = calcGoalsDifference(balancePoint)(matchPoint)
    val goalsConstant = calcGoalsConstantComponent

    if (goalsDifference < 0) WinB(goalsConstant, goalsConstant - goalsDifference)
    else if (goalsDifference == 0) Draw(goalsConstant)
    else WinA(goalsConstant + goalsDifference, goalsConstant)
  }

  def calcGoalsDifference(balancePoint: Double)(x: Double): Int = {
    val a1 = Math.abs(x - balancePoint)
    val a = Math.pow(a1, 1.3)
    val b = Math.floor(a * goalsDifferenceFactor)
    val abs = b.toInt

    if (x <= balancePoint) abs
    else -abs
  }

  def calcGoalsConstantComponent: Int = Math.round(Math.abs(scala.util.Random.nextGaussian() * goalConstantFactor)).toInt
}
