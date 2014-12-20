package service.game.evaluator

import models._match.Match
import models._match.result.MatchResult

trait MatchEvaluator {
  def eval(m: Match): MatchResult

  def calculateLoseProbability(m: Match): Double
  def calculateDrawProbability(m: Match): Double
  def calculateWinProbability(m: Match): Double
}
