package models.dedep.bonobo.core.round.group

import models.dedep.bonobo.core.Common
import Common._
import models.dedep.bonobo.core.round.group.GroupRound._
import models.dedep.bonobo.core.round.{Round, RoundUnit}
import models.dedep.bonobo.core.team.Team

import scala.util.Random

case class GroupRound(override val teams: List[Team],
                      override val pots: List[Pot] = Nil,
                      override val units: List[RoundUnit] = Nil,
                      override val stepIndex: Int = 0) extends Round {

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
