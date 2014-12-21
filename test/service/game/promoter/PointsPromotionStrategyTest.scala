package service.game.promoter

import models.team.Team
import models.tournament.GameRules
import models.unit.{UnitTeamResult, RoundUnit}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import models.Common._

class PointsPromotionStrategyTest extends Specification with Mockito {

  val strategy = new PointsPromotionStrategy

  implicit val rules = new GameRules(0, 1, 3)

  val t1 = new Team(1, 1, 1)
  val t2 = new Team(2, 2, 2)
  val t3 = new Team(3, 3, 8)
  val t4 = new Team(4, 4, 4)

  "should calculate points required to promotion" in {
    val unit = mock[RoundUnit]
    unit.matchesToPlay returns 3
    unit.promotedTeamsSize returns 2

    val r1 = new UnitTeamResult(t1, 9, 4, 0, 3, 0, 0)
    val r2 = new UnitTeamResult(t2, 6, 2, 2, 2, 0, 1)
    val r3 = new UnitTeamResult(t3, 3, 2, 3, 1, 0, 2)
    val r4 = new UnitTeamResult(t4, 0, 2, 10, 0, 0, 3)

    unit.teams returns List(t1, t2, t3, t4)
    unit.results returns List(r1, r2, r3, r4)

    strategy.getPointsRequiredToBePromoted(unit) mustEqual 12
  }

  "should calculate points required to elimination" in {
    val unit = mock[RoundUnit]
    unit.matchesToPlay returns 2
    unit.promotedTeamsSize returns 2

    val r1 = new UnitTeamResult(t1, 12, 4, 0, 4, 0, 0)
    val r2 = new UnitTeamResult(t2, 7, 2, 2, 2, 1, 1)
    val r3 = new UnitTeamResult(t3, 4, 2, 3, 1, 1, 2)
    val r4 = new UnitTeamResult(t4, 0, 2, 10, 0, 0, 4)

    unit.teams returns List(t1, t2, t3, t4)
    unit.results returns List(r1, r2, r3, r4)

    strategy.getPointsRequiredToBeEliminated(unit) mustEqual 1
  }

  "should arbitrate promotions and eliminations" in {
    val unit = mock[RoundUnit]
    unit.matchesToPlay returns 0
    unit.promotedTeamsSize returns 2

    val r1 = new UnitTeamResult(t1, 12, 4, 0, 4, 0, 0)
    val r2 = new UnitTeamResult(t3, 7, 2, 2, 2, 1, 1)
    val r3 = new UnitTeamResult(t2, 4, 2, 3, 1, 1, 2)
    val r4 = new UnitTeamResult(t4, 0, 2, 10, 0, 0, 4)

    unit.teams returns List(t1, t2, t3, t4)
    unit.results returns List(r1, r2, r3, r4)

    val result = strategy.findPromotedAndEliminatedTeams(unit)

    result._1 must containAllOf(Seq(t1, t3))
    result._2 must containAllOf(Seq(t2, t4))
  }
}
