package models.core._match.evaluator

import models.core._match.Match
import models.core._match.result.{Draw, WinA, WinB}
import models.core.team.Team
import org.scalatest.FunSuite

class NormalDistributionBasedMatchEvaluatorTest extends FunSuite {

  test("test balance point calculation - A 2x bigger than B") {
    //given
    val m = Match(new Team(1, 200, 0), new Team(2, 100, 0))

    //when
    val bp = NormalDistributionBasedMatchEvaluator.calcBalancePoint(m)

    //then
    assert(bp == 0.5)
  }

  test("test balance point calculation - B 2x bigger than A") {
    //given
    val m = Match(new Team(1, 100, 0), new Team(2, 200, 0))

    //when
    val bp = NormalDistributionBasedMatchEvaluator.calcBalancePoint(m)

    //then
    assert(bp == -0.5)
  }

  test("test balance point calculation - A equals B ") {
    //given
    val m = Match(new Team(1, 100, 0), new Team(2, 100, 0))

    //when
    val bp = NormalDistributionBasedMatchEvaluator.calcBalancePoint(m)

    //then
    assert(bp == 0)
  }

  test("test balance point calculation - A 4x bigger B ") {
    //given
    val m = Match(new Team(1, 400, 0), new Team(2, 100, 0))

    //when
    val bp = NormalDistributionBasedMatchEvaluator.calcBalancePoint(m)

    //then
    assert(bp == 1)
  }

  test("test balance point calculation - B 4x bigger A ") {
    //given
    val m = Match(new Team(1, 100, 0), new Team(2, 400, 0))

    //when
    val bp = NormalDistributionBasedMatchEvaluator.calcBalancePoint(m)

    //then
    assert(bp == -1)
  }

  test("test goals difference calculation - balanced situation") {
    //given
    val mp = 0d

    //when
    val diff = NormalDistributionBasedMatchEvaluator.calcGoalsDiff(mp)

    //then
    assert(diff == 0)
  }

  test("test goals difference calculation - draw edge situation") {
    //given
    val mp = 0.32

    //when
    val diff = NormalDistributionBasedMatchEvaluator.calcGoalsDiff(mp)

    //then
    assert(diff == 0)
  }

  test("test goals difference calculation - draw 2 edge situation") {
    //given
    val mp = -0.32

    //when
    val diff = NormalDistributionBasedMatchEvaluator.calcGoalsDiff(mp)

    //then
    assert(diff == 0)
  }

  test("test goals difference calculation - minimum win edge situation") {
    //given
    val mp = 0.34

    //when
    val diff = NormalDistributionBasedMatchEvaluator.calcGoalsDiff(mp)

    //then
    assert(diff == 1)
  }

  test("test goals difference calculation - minimum win 2 edge situation") {
    //given
    val mp = -0.34

    //when
    val diff = NormalDistributionBasedMatchEvaluator.calcGoalsDiff(mp)

    //then
    assert(diff == -1)
  }

  test("test goals difference calculation - -1.1 situation") {
    //given
    val mp = 1.1

    //when
    val diff = NormalDistributionBasedMatchEvaluator.calcGoalsDiff(mp)

    //then
    assert(diff == 3)
  }

  test("test goals difference calculation - 1.95 situation") {
    //given
    val mp = 1.95

    //when
    val diff = NormalDistributionBasedMatchEvaluator.calcGoalsDiff(mp)

    //then
    assert(diff == 5)
  }

  test("goals constant is not negative") {
    assert(NormalDistributionBasedMatchEvaluator.drawGoalsConstantComponent >= 0)
  }

  test("match point indicates Draw") {
    //given
    val mp = 0.2d

    //when
    val result = NormalDistributionBasedMatchEvaluator.createResultForMatchPoint(mp)

    assert(result.isInstanceOf[Draw])
  }

  test("match point indicates WinA") {
    //given
    val mp = 0.4d

    //when
    val result = NormalDistributionBasedMatchEvaluator.createResultForMatchPoint(mp)

    assert(result.isInstanceOf[WinA])
  }

  test("match point indicates WinB") {
    //given
    val mp = -1.4d

    //when
    val result = NormalDistributionBasedMatchEvaluator.createResultForMatchPoint(mp)

    assert(result.isInstanceOf[WinB])
  }
}
