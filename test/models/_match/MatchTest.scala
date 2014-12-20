package models._match

import models.Common
import Common._
import models._match.result.MatchResult
import models.team.Team
import modules.ServiceModule
import org.specs2.mutable.Specification
import scaldi.Injector

class MatchTest extends Specification {

  implicit val inj = new ServiceModule

  "eval generates result" in {
    //given
    val m = new Match(new Team(1, 100, 12), new Team(2, 100, 19))

    //when
    val result = m.eval()

    //then
    result mustNotEqual null
    result should beAnInstanceOf[MatchResult]
    result.aGoals must be >= 0
    result.bGoals must be >= 0
  }
}
