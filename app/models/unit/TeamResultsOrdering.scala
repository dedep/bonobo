package models.unit

import scala.util.Random

//todo: test
//todo: w przypadku takiego samego stosunku bramek powinny decydować bramki na wyjeździe
object TeamResultsOrdering extends scala.math.Ordering[UnitTeamResult] {
  override def compare(x: UnitTeamResult, y: UnitTeamResult): Int =
    comparePoints(x, y)
      .getOrElse(compareGoalsDifference(x, y)
      .getOrElse(randomComparision(x, y)))

  private def comparePoints(x: UnitTeamResult, y: UnitTeamResult): Option[Int] =
    if (x.points > y.points) Some(1)
    else if (x.points < y.points) Some(-1)
    else None

  private def compareGoalsDifference(x: UnitTeamResult, y: UnitTeamResult): Option[Int] =
    (x.goalsScored - x.goalsConceded) - (y.goalsScored - y.goalsConceded) match {
      case 0 => None
      case x: Int => Some(x)
    }

  private def randomComparision(x: UnitTeamResult, y: UnitTeamResult): Int = Random.nextBoolean() match {
    case false => -1
    case true  =>  1
  }
}
