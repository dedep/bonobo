package models.round

import com.typesafe.scalalogging.slf4j.Logger
import models.Common._
import models.reverse.TournamentInfo
import models.round.RoundStatus.RoundStatus
import models.territory.City
import models.unit
import models.unit.RoundUnit
import org.slf4j.LoggerFactory
import scaldi.Injector

import scala.util.Random

class GroupRound(override val name: String,
                 teamsCbn: => List[City],
                 potsCbn: => List[Pot] = Nil,
                 unitsCbn: => List[RoundUnit] = Nil,
                 override val stepIndex: Int = 0,
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
    require(pots.size == GroupRound.GROUP_SIZE, "Pots size must equal " + GroupRound.GROUP_SIZE)
    require(pots.tail.forall(_.size == pots.head.size), "All pots has to have the same size!")

    val shuffledPots: List[Pot] = for (pot <- pots) yield Random.shuffle(pot)
    val newUnits =
      for (i <- 0 until shuffledPots(0).size)
      yield new unit.Group("Group " + (65 + i).toChar, shuffledPots.map(_(i)))(toRoundInfo)

    new GroupRound(name, teams, pots, newUnits.toList, stepIndex, id, RoundStatus.PLAY_FIXTURE)(tournamentInfo)
  }

  override val isFinalRound: Boolean = false

  override def drawPots(): Round = {
    require(teams.size % GroupRound.GROUP_SIZE == 0)

    val potSize = teams.size / GroupRound.GROUP_SIZE
    val newPots = (0 until GroupRound.GROUP_SIZE).map(i => teams.sortBy(_.points).reverse.drop(i * potSize).take(potSize)).toList

    new GroupRound(name, teams, newPots, units, stepIndex, id, RoundStatus.DRAW_UNITS)(tournamentInfo)
  }

  override def playFixture(): Round = {
    require(units.nonEmpty)
    val newUnits = units.map(_.playFixture(stepIndex))

    if (stepIndex == GroupRound.LAST_INDEX) new GroupRound(name, teams, pots, newUnits, stepIndex + 1, id, RoundStatus.FINISHED)(tournamentInfo)
    else new GroupRound(name, teams, pots, newUnits, stepIndex + 1, id, RoundStatus.PLAY_FIXTURE)(tournamentInfo)
  }

  override val promotedTeamsCount: Int = GroupRound.PROMOTE_SIZE

  override val isPreliminary: Boolean = false
}

object GroupRound {
  val GROUP_SIZE = 4
  val PROMOTE_SIZE = 2
  val LAST_INDEX = ((GROUP_SIZE - 1) * 2) - 1
}