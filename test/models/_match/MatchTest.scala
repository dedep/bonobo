package models._match

import models._match.result.MatchResult
import models.reverse.{RoundUnitInfo, RoundInfo, TournamentInfo}
import models.team.Team
import models.tournament.GameRules
import modules.ServiceModule
import org.specs2.mutable.Specification

class MatchTest extends Specification {

  implicit val inj = new ServiceModule

  val tournamentInfo = new TournamentInfo("t", None, GameRules(0, 1, 3))
  val roundInfo = new RoundInfo(tournamentInfo)("rName", None)
  val unitInfo = new RoundUnitInfo(roundInfo)("uName", None)

  "eval generates result" in {
    //given
    val m = new Match(new Team(1, 100, 12), new Team(2, 100, 19))(unitInfo)

    //when
    val result = m.eval()

    //then
    result mustNotEqual null
    result should beAnInstanceOf[MatchResult]
    result.aGoals must be >= 0
    result.bGoals must be >= 0
  }
}
