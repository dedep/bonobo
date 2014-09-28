package models.round.pair

import com.typesafe.scalalogging.slf4j.Logger
import models.Common
import Common._
import models.Common
import models.round.{Round, RoundUnit}
import models.team.Team
import org.slf4j.LoggerFactory

import scala.util.Random

class PlayoffRound(override val name: String,
                   teamsCbn: => List[Team],
                   potsCbn: => List[Pot],
                   unitsCbn: => List[RoundUnit],
                   override val stepIndex: Int,
                   val preliminary: Boolean,
                   override val id: Option[Long] = None) extends Round {

  def this(nam: String,
      tms: => List[Team],
      pts: => List[Pot] = Nil,
      uts: => List[RoundUnit] = Nil,
      i: Int = 0,
      ids: Option[Long] = None) {

    this(nam, tms, pts, uts, i, false, ids)
  }

  require(teams.size % 2 == 0)

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  override lazy val teams = teamsCbn
  override lazy val pots = potsCbn
  override lazy val units = unitsCbn

  override def drawUnits(): Round = {
    require(pots.nonEmpty)
    require(pots.size == 2)
    require(pots(0).size == pots(1).size)

    val potA = Random.shuffle(pots(0))
    val potB = Random.shuffle(pots(1))

    new PlayoffRound(name, teams, pots, potA.zip(potB).map(a => new Pair("Pair " + a._1.name + " - " + a._2.name, a)), stepIndex, preliminary, id)
  }

  override def isFinalRound(): Boolean = !preliminary && teams.size == 2

  override def drawPots(): Round = {
    val potsTuple = teams.sortBy(_.rankPoints).reverse.splitAt(teams.size / 2)
    new PlayoffRound(name, teams, List(potsTuple._1, potsTuple._2), units, stepIndex, preliminary, id)
  }

  override def isFinished(): Boolean = stepIndex == 2

  override def playFixture(): Round = {
    require(units.nonEmpty)

    val newUnits = units.map(_.playFixture(stepIndex))
    new PlayoffRound(name, teams, pots, newUnits, stepIndex + 1, preliminary, id)
  }

  override def getPromotedTeamsCount: Int = 1
}
