package models.round

import models.Common._
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
  val status: RoundStatus.RoundStatus

  val isFinalRound: Boolean
  val promotedTeamsCount: Int
  val isPreliminary: Boolean

  def getPromotedTeams(implicit strategy: PromotionsStrategy): List[Team] =
    if (!isFinished) throw new IllegalStateException("Round has not been finished yet")
    else units.flatMap(_.promotedTeams)

  def getEliminatedTeams(implicit strategy: PromotionsStrategy): List[Team] =
    teams.diff(getPromotedTeams)

  def doStep(): Round = status match {
      case RoundStatus.DRAW_POTS    => drawPots()
      case RoundStatus.DRAW_UNITS   => drawUnits()
      case RoundStatus.PLAY_FIXTURE => playFixture()
      case RoundStatus.FINISHED     => throw new IllegalStateException("Trying to do step in finished round")
  }

  def drawUnits(): Round
  def drawPots(): Round
  def playFixture(): Round
  
  val isFinished = status == RoundStatus.FINISHED
  val canBePlayed = status == RoundStatus.PLAY_FIXTURE
}