package models.round

import models.Common
import Common._
import models.Common
import models.round.promotion.PromotionsStrategy
import models.team.Team

//TODO: przydałoby się rozróżnienie na Final Round - tam np. nie ma losowania
trait Round {
  val teams: List[Team]
  val pots: List[Pot]
  val units: List[RoundUnit]
  val stepIndex: Int
  val id: Option[Long]
  val name: String

  def isFinalRound(): Boolean

  def isFinished(): Boolean

  def getPromotedTeamsCount: Int

  //todo: TRZEBA to persystować - bo są rozbieżności w kolejności gdy o awansie decyduje los
  def getPromotedTeams(implicit strategy: PromotionsStrategy): List[Team] =
    if (!isFinished()) throw new IllegalStateException("Round has not been finished yet")
    else strategy.arbitratePromotions(this)

  def doStep(): Round = status match {
      case RoundStatus.DRAW_POTS    => drawPots()
      case RoundStatus.DRAW_UNITS   => drawUnits()
      case RoundStatus.PLAY_FIXTURE => playFixture()
      case RoundStatus.FINISHED     => throw new IllegalStateException("Trying to do step in finished round")
  }

  def drawUnits(): Round

  def drawPots(): Round

  def playFixture(): Round

  val status: RoundStatus.RoundStatus = {
    if (isFinished()) RoundStatus.FINISHED
    else if (pots.isEmpty) RoundStatus.DRAW_POTS
    else if (units.isEmpty) RoundStatus.DRAW_UNITS
    else RoundStatus.PLAY_FIXTURE
  }

  val isPlayed = status == RoundStatus.PLAY_FIXTURE

  object RoundStatus extends Enumeration {
    val DRAW_POTS, DRAW_UNITS, PLAY_FIXTURE, FINISHED = Value

    type RoundStatus = Value
  }
}