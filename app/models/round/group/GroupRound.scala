package models.round.group

import com.typesafe.scalalogging.slf4j.Logger
import models.Common
import Common._
import models.round.RoundStatus.RoundStatus
import models.round.RoundStatus.RoundStatus
import models.round.group.GroupRound._
import models.round.{RoundStatus, Round, RoundUnit}
import models.team.Team
import org.slf4j.LoggerFactory

import scala.util.Random

class GroupRound(override val name: String,
                 teamsCbn: => List[Team],
                 potsCbn: => List[Pot] = Nil,
                 unitsCbn: => List[RoundUnit] = Nil,
                 override val stepIndex: Int = 0,
                 override val id: Option[Long] = None,
                 override val status: RoundStatus = RoundStatus.DRAW_POTS) extends Round {

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  override lazy val teams = teamsCbn
  override lazy val pots = potsCbn
  override lazy val units = unitsCbn

  override def drawUnits(): Round = {
    require(pots.nonEmpty)
    require(pots.size == GROUP_SIZE, "Pots size must equal " + GROUP_SIZE)
    require(pots.tail.forall(_.size == pots.head.size), "All pots has to have the same size!")

    val shuffledPots: List[Pot] = for (pot <- pots) yield Random.shuffle(pot)
    val newUnits = for (i <- 0 until shuffledPots(0).size) yield new Group("Group " + (65 + i).toChar, shuffledPots.map(_(i)))

    new GroupRound(name, teams, pots, newUnits.toList, stepIndex, id, RoundStatus.PLAY_FIXTURE)
  }

  override val isFinalRound: Boolean = false

  override def drawPots(): Round = {
    require(teams.size % GROUP_SIZE == 0)

    val potSize = teams.size / GROUP_SIZE
    val newPots = (0 until GROUP_SIZE).map(i => teams.sortBy(_.rankPoints).reverse.drop(i * potSize).take(potSize)).toList

    new GroupRound(name, teams, newPots, units, stepIndex, id, RoundStatus.DRAW_UNITS)
  }

  override def playFixture(): Round = {
    require(units.nonEmpty)
    val newUnits = units.map(_.playFixture(stepIndex))

    if (stepIndex == LAST_INDEX) new GroupRound(name, teams, pots, newUnits, stepIndex + 1, id, RoundStatus.FINISHED)
    else new GroupRound(name, teams, pots, newUnits, stepIndex + 1, id, RoundStatus.PLAY_FIXTURE)
  }

  override val promotedTeamsCount: Int = PROMOTE_SIZE

  override val isPreliminary: Boolean = false
}

object GroupRound {
  val GROUP_SIZE = 4
  val PROMOTE_SIZE = 2
  val LAST_INDEX = ((GROUP_SIZE - 1) * 2) - 1
}