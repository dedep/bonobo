package service.game.promoter

import models.Common
import models.Common._
import models.team.Team
import models.unit.{RoundUnit, UnitTeamResult}

import scala.util.Sorting

class PointsPromotionStrategy extends PromotionsStrategy {

  override def findPromotedAndEliminatedTeams(unit: RoundUnit): (List[Team], List[Team]) = {
    if (unit.matchesToPlay == 0) {
      val promotions = arbitrateFinalPromotions(unit)
      val eliminations = unit.teams.diff(promotions)

      (promotions, eliminations)
    } else {
      val promotions = arbitratePromotions(unit)
      val eliminations = arbitrateEliminations(unit)

      (promotions, eliminations)
    }
  }

  private def arbitratePromotions(unit: RoundUnit): List[Team] =
    findTeamsWithResult(unit, _.points > getPointsRequiredToBePromoted(unit))

  private def arbitrateEliminations(unit: RoundUnit): List[Team] =
    findTeamsWithResult(unit, _.points < getPointsRequiredToBeEliminated(unit))

  private def findTeamsWithResult(unit: RoundUnit, f: (UnitTeamResult) => Boolean) =
    unit.results.filter(f).map(_.team)

  //todo: testy
  private def getPointsRequiredToBePromoted(unit: RoundUnit): Double =
    getNthTeamPoints(unit, unit.promotedTeamsSize) + (3 * unit.matchesToPlay)  // todo: trójka

  private def getPointsRequiredToBeEliminated(unit: RoundUnit): Double =
    getNthTeamPoints(unit, unit.promotedTeamsSize - 1) - (3 * unit.matchesToPlay) // todo: trójka

  private def getNthTeamPoints(unit: RoundUnit, n: Int) =
    Sorting.stableSort(unit.results).reverse(n).points

  private def arbitrateFinalPromotions(unit: RoundUnit): List[Team] =
    Sorting.stableSort(unit.results).reverse.take(unit.promotedTeamsSize).map(_.team).toList
}