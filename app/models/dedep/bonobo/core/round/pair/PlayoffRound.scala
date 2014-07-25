package models.dedep.bonobo.core.round.pair

import models.dedep.bonobo.core.Common
import Common._
import models.dedep.bonobo.core.round.{Round, RoundUnit}
import models.dedep.bonobo.core.team.Team

import scala.util.Random

case class PlayoffRound(override val teams: List[Team],
                        override val pots: List[Pot] = Nil,
                        override val units: List[RoundUnit] = Nil,
                        override val stepIndex: Int = 0,
                        preliminary: Boolean = false) extends Round {

  require(teams.size % 2 == 0)

  override def drawUnits(): Round = {
    require(pots.nonEmpty)
    require(pots.size == 2)
    require(pots(0).size == pots(1).size)

    val potA = Random.shuffle(pots(0))
    val potB = Random.shuffle(pots(1))

    new PlayoffRound(this.teams, this.pots, potA.zip(potB).map(new Pair(_)), stepIndex, preliminary)
  }

  override def isFinalRound(): Boolean = !preliminary && teams.size == 2

  override def drawPots(): Round = {
    val potsTuple = teams.sortBy(_.rankPoints).reverse.splitAt(teams.size / 2)
    new PlayoffRound(this.teams, List(potsTuple._1, potsTuple._2), this.units, stepIndex, preliminary)
  }

  override def isFinished(): Boolean = stepIndex == 2

  override def playFixture(): Round = {
    require(units.nonEmpty)

    val newUnits = units.map(_.playFixture(stepIndex))
    new PlayoffRound(this.teams, this.pots, newUnits, stepIndex + 1, preliminary)
  }

  override def getPromotedTeamsCount: Int = 1
}
