package models.dedep.bonobo.core._match

import models.dedep.bonobo.core.Common._
import models.dedep.bonobo.core._match.result.MatchResult
import models.dedep.bonobo.core.team.Team
import org.scalatest.FunSuite

class MatchTest extends FunSuite {
  test("to string format") {
    //given
    val m = Match(new Team(100, 12), new Team(80, 19))

    //when
    val format = m.toString

    //then
    assert(format == "12 vs 19")
  }

  test("eval generates result") {
    for (i <- 0 to 10) {
      //given
      val m = Match(new Team(100, 12), new Team(100, 19))

      //when
      val result = m.eval

      //then
      assert(result != null)
      assert(result.isInstanceOf[MatchResult])
      assert(result.aGoals >= 0)
      assert(result.bGoals >= 0)
    }
  }
}
