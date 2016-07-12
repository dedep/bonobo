package models.round

import models.Common._
import models.reverse.{RoundInfo, TournamentInfo}
import models.territory.City
import models.unit.RoundUnit

//TODO: przydałoby się rozróżnienie na Final Round - tam np. nie ma losowania
trait Round {
  val teams: List[City]
  val pots: List[Pot]
  val units: List[RoundUnit]
  val stepIndex: Int
  val id: Option[Long]
  val name: String
  val status: RoundStatus.RoundStatus

  val isFinalRound: Boolean
  val promotedTeamsCount: Int
  val isPreliminary: Boolean

  val tournamentInfo: TournamentInfo

  def getPromotedTeams: List[City] =
    if (!isFinished) throw new IllegalStateException("Round has not been finished yet")
    else units.flatMap(_.promotedTeams)

  def getEliminatedTeams: List[City] =
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

  protected val toRoundInfo = new RoundInfo(tournamentInfo)(name, id)
}