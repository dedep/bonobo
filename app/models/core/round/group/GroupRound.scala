package models.core.round.group

import models.core.Common
import Common._
import models.core.round.group.GroupRound._
import models.core.round.{Round, RoundUnit}
import models.core.team.Team

import scala.util.Random

class GroupRound(teamsCbn: => List[Team],
                      potsCbn: => List[Pot] = Nil,
                      unitsCbn: => List[RoundUnit] = Nil,
                      override val stepIndex: Int = 0) extends Round {

  require(teams.size % GROUP_SIZE == 0)

  override lazy val teams = teamsCbn
  override lazy val pots = potsCbn
  override lazy val units = unitsCbn

  override def drawUnits(): Round = {
    require(pots.nonEmpty)
    require(pots.size == GROUP_SIZE, "Pots size must equal " + GROUP_SIZE)
    require(pots.tail.forall(_.size == pots.head.size), "All pots has to have the same size!")

    val shuffledPots: List[Pot] = for (pot <- pots) yield Random.shuffle(pot)
    val newUnits =
      (0 until shuffledPots(0).size)
        .map(i => shuffledPots.map(_(i)))
        .map(new Group(_))
        .toList

    new GroupRound(this.teams, this.pots, newUnits, this.stepIndex)
  }

  override def isFinalRound(): Boolean = false

  override def drawPots(): Round = {
    require(teams.size % GROUP_SIZE == 0)

    val potSize = teams.size / GROUP_SIZE
    val newPots = (0 until GROUP_SIZE).map(i => teams.sortBy(_.rankPoints).reverse.drop(i * potSize).take(potSize)).toList

    new GroupRound(this.teams, newPots, this.units, this.stepIndex)
  }

  override def isFinished(): Boolean = stepIndex == (GROUP_SIZE - 1) * 2

  override def playFixture(): Round = {
    require(units.nonEmpty)

    val newUnits = units.map(_.playFixture(stepIndex))
    new GroupRound(this.teams, this.pots, newUnits, stepIndex + 1)
  }

  override def getPromotedTeamsCount: Int = GROUP_SIZE / 2
}

object GroupRound {
  val GROUP_SIZE = 4
  val PROMOTE_SIZE = 2
}
