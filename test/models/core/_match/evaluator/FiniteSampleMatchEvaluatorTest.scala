package models.core._match.evaluator

import org.scalatest.FunSuite

class FiniteSampleMatchEvaluatorTest extends FunSuite {

  test("test goals difference calculation - balanced situation") {
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(0) == 6)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(2) == 6)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(3) == 5)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(5) == 5)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(6) == 4)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(9) == 4)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(10) == 3)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(13) == 3)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(14) == 2)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(16) == 2)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(18) == 2)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(19) == 1)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(22) == 1)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(23) == 1)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(24) == 0)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(36) == 0)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(37) == -1)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(38) == -1)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(41) == -1)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(42) == -2)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(44) == -2)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(46) == -2)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(47) == -3)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(50) == -3)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(51) == -4)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(54) == -4)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(55) == -5)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(57) == -5)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(58) == -6)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(30)(60) == -6)
  }

  test("test goals difference calculation - edge situation") {
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(60) == 0)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(54) == 0)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(53) == 1)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(49) == 1)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(48) == 2)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(44) == 2)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(43) == 3)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(40) == 3)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(39) == 4)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(36) == 4)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(35) == 5)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(33) == 5)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(32) == 6)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(29) == 6)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(28) == 7)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(26) == 7)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(25) == 8)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(23) == 8)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(22) == 9)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(19) == 9)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(18) == 10)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(16) == 10)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(15) == 11)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(13) == 11)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(12) == 12)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(10) == 12)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(9) == 13)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(7) == 13)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(6) == 14)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(4) == 14)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(3) == 15)
    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(1) == 15)

    assert(FiniteSampleMatchEvaluator.calcGoalsDifference(60)(0) == 16)
  }
}
