package models.core._match

import models.core.Common._
import models.core._match.result.MatchResult
import models.core.team.Team
import org.scalatest.FunSuite

class MatchTest extends FunSuite {
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
