package models.core.round.group

import models.core.Common._
import models.core._match.PlayedMatch
import models.core.team.Team
import org.scalatest.FunSuite

class GroupRoundTest extends FunSuite {

  test("is final round") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)

    //when
    val r = new GroupRound("", List(t1, t2, t3, t4))

    //then
    assert(!r.isFinalRound())
  }

  test("teams test") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val t5 = new Team(5, 5, 5)
    val t6 = new Team(6, 6, 6)
    val t7 = new Team(7, 7, 7)
    val t8 = new Team(8, 8, 8)

    //when
    val r = new GroupRound("", List(t1, t2, t3, t4, t5, t6, t7, t8))

    //then
    assert(r.teams.size == 8)

    assert(r.teams(0) == t1)
    assert(r.teams(1) == t2)
    assert(r.teams(2) == t3)
    assert(r.teams(3) == t4)
    assert(r.teams(4) == t5)
    assert(r.teams(5) == t6)
    assert(r.teams(6) == t7)
    assert(r.teams(7) == t8)
  }

  test("teams pots drawing") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 8)
    val t4 = new Team(4, 4, 4)
    val t5 = new Team(5, 5, 5)
    val t6 = new Team(6, 6, 6)
    val t7 = new Team(7, 7, 7)
    val t8 = new Team(8, 8, 3)

    val r = new GroupRound("", List(t1, t2, t3, t4, t5, t6, t7, t8))

    //when
    val drawnPotsRound = r.drawPots()

    //then
    assert(drawnPotsRound.teams.size == 8)
    assert(drawnPotsRound.stepIndex == 0)
    assert(drawnPotsRound.units == Nil)

    assert(drawnPotsRound.pots.size == 4)
    assert(drawnPotsRound.pots(0).contains(t3))
    assert(drawnPotsRound.pots(0).contains(t7))
    assert(drawnPotsRound.pots(1).contains(t5))
    assert(drawnPotsRound.pots(1).contains(t6))
    assert(drawnPotsRound.pots(2).contains(t8))
    assert(drawnPotsRound.pots(2).contains(t4))
    assert(drawnPotsRound.pots(3).contains(t1))
    assert(drawnPotsRound.pots(3).contains(t2))
  }

  test("teams units drawing") {
    for (i <- 0 to 15) {
      //given
      val t1 = new Team(1, 1, 1)
      val t2 = new Team(2, 2, 2)
      val t3 = new Team(3, 3, 8)
      val t4 = new Team(4, 4, 4)
      val t5 = new Team(5, 5, 5)
      val t6 = new Team(6, 6, 6)
      val t7 = new Team(7, 7, 7)
      val t8 = new Team(8, 8, 3)

      val r = new GroupRound("", List(t1, t2, t3, t4, t5, t6, t7, t8))

      //when
      val drawnUnitsRound = r.drawPots().drawUnits()

      //then
      assert(drawnUnitsRound.teams.size == 8)
      assert(drawnUnitsRound.stepIndex == 0)
      assert(drawnUnitsRound.pots.size == 4)

      assert(drawnUnitsRound.units.size == 2)
      assert(drawnUnitsRound.units(0).teams.filter(t => t == t3 || t == t7).nonEmpty)
      assert(drawnUnitsRound.units(0).teams.filter(t => t == t5 || t == t6).nonEmpty)
      assert(drawnUnitsRound.units(0).teams.filter(t => t == t8 || t == t4).nonEmpty)
      assert(drawnUnitsRound.units(0).teams.filter(t => t == t1 || t == t2).nonEmpty)

      assert(drawnUnitsRound.units(1).teams.filter(t => t == t3 || t == t7).nonEmpty)
      assert(drawnUnitsRound.units(1).teams.filter(t => t == t5 || t == t6).nonEmpty)
      assert(drawnUnitsRound.units(1).teams.filter(t => t == t8 || t == t4).nonEmpty)
      assert(drawnUnitsRound.units(1).teams.filter(t => t == t1 || t == t2).nonEmpty)
    }
  }

  test("play fixture") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 8)
    val t4 = new Team(4, 4, 4)
    val t5 = new Team(5, 5, 5)
    val t6 = new Team(6, 6, 6)
    val t7 = new Team(7, 7, 7)
    val t8 = new Team(8 ,8, 3)

    val r = new GroupRound("", List(t1, t2, t3, t4, t5, t6, t7, t8))

    //when
    val round = r.drawPots().drawUnits().playFixture()

    //then
    assert(round.teams.size == 8)
    assert(round.stepIndex == 1)
    assert(round.pots.size == 4)
    assert(round.units.size == 2)

    assert(round.units(0).results.forall(r => r.points + r.goalsConceded > 0))
    assert(round.units(0).fixtures(0).forall(_.isInstanceOf[PlayedMatch]))

    assert(round.units(1).results.forall(r => r.points + r.goalsConceded > 0))
    assert(round.units(1).fixtures(0).forall(_.isInstanceOf[PlayedMatch]))
  }

  test("stepping test") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 8)
    val t4 = new Team(4, 4, 4)
    val t5 = new Team(5, 5, 5)
    val t6 = new Team(6, 6, 6)
    val t7 = new Team(7, 7, 7)
    val t8 = new Team(8, 8, 3)

    val r = new GroupRound("", List(t1, t2, t3, t4, t5, t6, t7, t8))

    assert(r.teams.size == 8)
    assert(r.pots == Nil)
    assert(r.units == Nil)
    assert(r.stepIndex == 0)

    val r1 = r.doStep()

    assert(r1.teams.size == 8)
    assert(r1.pots.size == 4)
    assert(r1.units == Nil)
    assert(r1.stepIndex == 0)

    val r2 = r1.doStep()

    assert(r2.teams.size == 8)
    assert(r2.pots.size == 4)
    assert(r2.units.size == 2)
    assert(r2.stepIndex == 0)

    val r3 = r2.doStep()

    assert(r3.teams.size == 8)
    assert(r3.pots.size == 4)
    assert(r3.units.size == 2)
    assert(r3.stepIndex == 1)

    val r4 = r3.doStep()

    assert(r4.teams.size == 8)
    assert(r4.pots.size == 4)
    assert(r4.units.size == 2)
    assert(r4.stepIndex == 2)

    val rn = r4.doStep().doStep().doStep().doStep()

    try {
      rn.doStep()
      fail("Stepping test supposed to end with exception thrown")
    } catch {
      case _: IllegalStateException => None
      case _: Throwable => fail("Stepping test supposed to end with IllegalStateException thrown")
    }
  }

  test("promoted teams test") {
    for (i <- 0 to 30) {
      //given
      val t1 = new Team(1, 1, 1)
      val t2 = new Team(2, 2, 2)
      val t3 = new Team(3, 3, 8)
      val t4 = new Team(4, 4, 4)
      val t5 = new Team(5, 5, 5)
      val t6 = new Team(6, 6, 6)
      val t7 = new Team(7, 7, 7)
      val t8 = new Team(8, 8, 3)

      val r = new GroupRound("", List(t1, t2, t3, t4, t5, t6, t7, t8))

      //when
      val round = r.doStep().doStep().doStep().doStep().doStep().doStep().doStep().doStep()
      val promotedTeams = round.getPromotedTeams
      val notPromotedTeams = round.teams.diff(promotedTeams)

      val promotedResult = round.units(0).results
        .filter(tr => tr.team == promotedTeams(0) || tr.team == promotedTeams(1) || tr.team == promotedTeams(2) || tr.team == promotedTeams(3))
      val notPromotedResult = round.units(0).results
        .filter(tr => tr.team == notPromotedTeams(0) || tr.team == notPromotedTeams(1) || tr.team == notPromotedTeams(2) || tr.team == notPromotedTeams(3))

      //then
      assert(promotedTeams.size == 4)
      assert(notPromotedTeams.size == 4)

      assert(promotedResult.head.points >= notPromotedResult.head.points)
      assert(promotedResult.head.points >= notPromotedResult.tail.head.points)
      assert(promotedResult.tail.head.points >= notPromotedResult.head.points)
      assert(promotedResult.tail.head.points >= notPromotedResult.tail.head.points)

      if (promotedResult.head.points == notPromotedResult.head.points) {
        assert(promotedResult.head.goalsScored - promotedResult.head.goalsConceded >=
          notPromotedResult.head.goalsScored - notPromotedResult.head.goalsConceded)
      }

      if (promotedResult.head.points == notPromotedResult.tail.head.points) {
        assert(promotedResult.head.goalsScored - promotedResult.head.goalsConceded >=
          notPromotedResult.tail.head.goalsScored - notPromotedResult.tail.head.goalsConceded)
      }

      if (promotedResult.tail.head.points == notPromotedResult.head.points) {
        assert(promotedResult.tail.head.goalsScored - promotedResult.tail.head.goalsConceded >=
          notPromotedResult.head.goalsScored - notPromotedResult.head.goalsConceded)
      }

      if (promotedResult.tail.head.points == notPromotedResult.tail.head.points) {
        assert(promotedResult.tail.head.goalsScored - promotedResult.tail.head.goalsConceded >=
          notPromotedResult.tail.head.goalsScored - notPromotedResult.tail.head.goalsConceded)
      }
    }
  }
}
