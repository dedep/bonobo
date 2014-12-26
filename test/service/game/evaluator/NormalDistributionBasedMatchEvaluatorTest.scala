package service.game.evaluator

import models._match.result.{Draw, WinA, WinB}
import models.reverse.{TournamentInfo, RoundInfo, RoundUnitInfo}
import models.team.Team
import models.tournament.GameRules
import org.scalatest.FunSuite
import models._match.Match
import scaldi.Injector

class NormalDistributionBasedMatchEvaluatorTest(implicit inj: Injector) extends FunSuite {

  val evaluator = new NormalDistributionBasedMatchEvaluator

  val tournamentInfo = new TournamentInfo("t", None, GameRules(0, 1, 3))
  val roundInfo = new RoundInfo(tournamentInfo)("rName", None)
  val unitInfo = new RoundUnitInfo(roundInfo)("uName", None)

  test("test balance point calculation - A 2x bigger than B") {
    //given
    val m = new Match(new Team(1, 200, 0), new Team(2, 100, 0))(unitInfo)

    //when
    val bp = evaluator.calcBalancePoint(m)

    //then
    assert(bp == 0.65)
  }

  test("test balance point calculation - B 2x bigger than A") {
    //given
    val m = new Match(new Team(1, 100, 0), new Team(2, 200, 0))(unitInfo)

    //when
    val bp = evaluator.calcBalancePoint(m)

    //then
    assert(bp == -0.35)
  }

  test("test balance point calculation - A equals B ") {
    //given
    val m = new Match(new Team(1, 100, 0), new Team(2, 100, 0))(unitInfo)

    //when
    val bp = evaluator.calcBalancePoint(m)

    //then
    assert(bp == 0.15)
  }

  test("test balance point calculation - A 4x bigger B ") {
    //given
    val m = new Match(new Team(1, 400, 0), new Team(2, 100, 0))(unitInfo)

    //when
    val bp = evaluator.calcBalancePoint(m)

    //then
    assert(bp == 1.15)
  }

  test("test balance point calculation - B 4x bigger A ") {
    //given
    val m = new Match(new Team(1, 100, 0), new Team(2, 400, 0))(unitInfo)

    //when
    val bp = evaluator.calcBalancePoint(m)

    //then
    assert(bp == -0.85)
  }

  test("test goals difference calculation - balanced situation") {
    //given
    val mp = 0d

    //when
    val diff = evaluator.calcGoalsDiff(mp)

    //then
    assert(diff == 0)
  }

  test("test goals difference calculation - draw edge situation") {
    //given
    val mp = 0.32

    //when
    val diff = evaluator.calcGoalsDiff(mp)

    //then
    assert(diff == 0)
  }

  test("test goals difference calculation - draw 2 edge situation") {
    //given
    val mp = -0.32

    //when
    val diff = evaluator.calcGoalsDiff(mp)

    //then
    assert(diff == 0)
  }

  test("test goals difference calculation - minimum win edge situation") {
    //given
    val mp = 0.34

    //when
    val diff = evaluator.calcGoalsDiff(mp)

    //then
    assert(diff == 1)
  }

  test("test goals difference calculation - minimum win 2 edge situation") {
    //given
    val mp = -0.34

    //when
    val diff = evaluator.calcGoalsDiff(mp)

    //then
    assert(diff == -1)
  }

  test("test goals difference calculation - -1.1 situation") {
    //given
    val mp = 1.1

    //when
    val diff = evaluator.calcGoalsDiff(mp)

    //then
    assert(diff == 3)
  }

  test("test goals difference calculation - 1.95 situation") {
    //given
    val mp = 1.95

    //when
    val diff = evaluator.calcGoalsDiff(mp)

    //then
    assert(diff == 5)
  }

  test("goals constant is not negative") {
    assert(evaluator.drawGoalsConstantComponent >= 0)
  }

  test("match point indicates Draw") {
    //given
    val mp = 0.2d

    //when
    val result = evaluator.createResultForMatchPoint(mp)

    assert(result.isInstanceOf[Draw])
  }

  test("match point indicates WinA") {
    //given
    val mp = 0.4d

    //when
    val result = evaluator.createResultForMatchPoint(mp)

    assert(result.isInstanceOf[WinA])
  }

  test("match point indicates WinB") {
    //given
    val mp = -1.4d

    //when
    val result = evaluator.createResultForMatchPoint(mp)

    assert(result.isInstanceOf[WinB])
  }

  test("match lose probability - balanced situation") {
    //given
    val m = new Match(new Team(1, 200, 0), new Team(2, 200, 0))(unitInfo)

    //when
    val result = evaluator.calculateLoseProbability(m)

    assert(result > 0.314)
    assert(result < 0.315)
  }

  test("match win probability - balanced situation") {
    //given
    val m = new Match(new Team(1, 200, 0), new Team(2, 200, 0))(unitInfo)

    //when
    val result = evaluator.calculateWinProbability(m)

    assert(result > 0.427)
    assert(result < 0.428)
  }

  test("match draw probability - balanced situation") {
    //given
    val m = new Match(new Team(1, 200, 0), new Team(2, 200, 0))(unitInfo)

    //when
    val result = evaluator.calculateDrawProbability(m)

    assert(result > 0.258)
    assert(result < 0.259)
  }

  test("match lose probability - doubled situation") {
    //given
    val m = new Match(new Team(1, 200, 0), new Team(2, 100, 0))(unitInfo)

    //when
    val result = evaluator.calculateLoseProbability(m)

    assert(result > 0.162)
    assert(result < 0.163)
  }

  test("match draw probability - doubled situation") {
    //given
    val m = new Match(new Team(1, 200, 0), new Team(2, 100, 0))(unitInfo)

    //when
    val result = evaluator.calculateDrawProbability(m)

    assert(result > 0.212)
    assert(result < 0.214)
  }

  test("match win probability - doubled situation") {
    //given
    val m = new Match(new Team(1, 200, 0), new Team(2, 100, 0))(unitInfo)

    //when
    val result = evaluator.calculateWinProbability(m)

    assert(result > 0.624)
    assert(result < 0.625)
  }
}
