package models.round

import com.typesafe.scalalogging.slf4j.Logger
import models.Common._
import models.reverse.TournamentInfo
import models.round.RoundStatus._
import models.team.Team
import models.unit.{Pair, RoundUnit}
import org.slf4j.LoggerFactory
import scaldi.Injector

import scala.util.Random

class PlayoffRound(override val name: String,
                   teamsCbn: => List[Team],
                   potsCbn: => List[Pot] = Nil,
                   unitsCbn: => List[RoundUnit] = Nil,
                   override val stepIndex: Int = 0,
                   override val isPreliminary: Boolean = false,
                   override val id: Option[Long] = None,
                   override val status: RoundStatus = RoundStatus.DRAW_POTS)
                  (override val tournamentInfo: TournamentInfo)
                  (implicit inj: Injector) extends Round {

  private val log = Logger(LoggerFactory.getLogger("app"))

  override lazy val teams = teamsCbn
  override lazy val pots = potsCbn
  override lazy val units = unitsCbn

  override def drawUnits(): Round = {
    require(pots.nonEmpty)
    require(pots.size == 2)
    require(pots(0).size == pots(1).size)

    val potA = Random.shuffle(pots(0))
    val potB = Random.shuffle(pots(1))

    new PlayoffRound(name, teams, pots, potA.zip(potB).map(a => new Pair("Pair " + a._1.name + " - " + a._2.name, a)(toRoundInfo)),
      stepIndex, isPreliminary, id, RoundStatus.PLAY_FIXTURE)(toRoundInfo)
  }

  override lazy val isFinalRound: Boolean = !isPreliminary && teams.size == 2

  override def drawPots(): Round = {
    val potsTuple = teams.sortBy(_.rankPoints).reverse.splitAt(teams.size / 2)
    new PlayoffRound(name, teams, List(potsTuple._1, potsTuple._2), units, stepIndex, isPreliminary, id, RoundStatus.DRAW_UNITS)(tournamentInfo)
  }

  override def playFixture(): Round = {
    require(units.nonEmpty)
    val newUnits = units.map(_.playFixture(stepIndex))

    if (stepIndex == 1) new PlayoffRound(name, teams, pots, newUnits, stepIndex + 1, isPreliminary, id, RoundStatus.FINISHED)(tournamentInfo)
    else new PlayoffRound(name, teams, pots, newUnits, stepIndex + 1, isPreliminary, id, RoundStatus.PLAY_FIXTURE)(tournamentInfo)
  }

  override val promotedTeamsCount: Int = 1
}
