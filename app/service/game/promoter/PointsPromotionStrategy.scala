package service.game.promoter

import models.Common._
import models.territory.City
import models.tournament.GameRules
import models.unit.{RoundUnit, UnitTeamResult}

import scala.util.Sorting

//todo: nie uwzględnia faktycznego terminarza. może być problem przy niestandardowej konfiguracji,
//todo: np. group-3
class PointsPromotionStrategy extends PromotionsStrategy {

  override def findPromotedAndEliminatedTeams(unit: RoundUnit)(implicit gm: GameRules): (List[City], List[City]) = {
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

  private def arbitratePromotions(unit: RoundUnit)(implicit gm: GameRules): List[City] =
    findTeamsWithResult(unit, _.points > getPointsRequiredToBePromoted(unit))

  private def arbitrateEliminations(unit: RoundUnit)(implicit gm: GameRules): List[City] =
    findTeamsWithResult(unit, _.points < getPointsRequiredToBeEliminated(unit))

  private def findTeamsWithResult(unit: RoundUnit, f: (UnitTeamResult) => Boolean) =
    unit.results.filter(f).map(_.team)

  def getPointsRequiredToBePromoted(unit: RoundUnit)(implicit gm: GameRules): Double =
    getNthTeamPoints(unit, unit.promotedTeamsSize) + (gm.winPoints * unit.matchesToPlay)

  //todo: nie uwzględnia różnych kombinacji GameRules, np. punkty za porażkę
  def getPointsRequiredToBeEliminated(unit: RoundUnit)(implicit gm: GameRules): Double =
    getNthTeamPoints(unit, unit.promotedTeamsSize - 1) - (gm.winPoints * unit.matchesToPlay)

  private def getNthTeamPoints(unit: RoundUnit, n: Int) =
    Sorting.stableSort(unit.results).reverse(n).points

  private def arbitrateFinalPromotions(unit: RoundUnit): List[City] =
    Sorting.stableSort(unit.results).reverse.take(unit.promotedTeamsSize).map(_.team).toList
}