package models.round.promotion

import scala.util.Random

import models.round.result.TeamResult

//todo: w przypadku takiego samego stosunku bramek powinny decydować bramki na wyjeździe
object TeamResultsOrdering extends scala.math.Ordering[TeamResult] {
  override def compare(x: TeamResult, y: TeamResult): Int =
    comparePoints(x, y)
      .getOrElse(compareGoalsDifference(x, y)
      .getOrElse(randomComparision(x, y)))

  private def comparePoints(x: TeamResult, y: TeamResult): Option[Int] =
    if (x.points > y.points) Some(1)
    else if (x.points < y.points) Some(-1)
    else None

  private def compareGoalsDifference(x: TeamResult, y: TeamResult): Option[Int] =
    (x.goalsScored - x.goalsConceded) - (y.goalsScored - y.goalsConceded) match {
      case 0 => None
      case x: Int => Some(x)
    }

  private def randomComparision(x: TeamResult, y: TeamResult): Int = Random.nextBoolean() match {
    case false => -1
    case true  =>  1
  }
}