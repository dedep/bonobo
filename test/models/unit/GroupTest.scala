package models.unit

import models.Common._
import models._match.PlayedMatch
import models._match.result.{Draw, WinA}
import models.reverse.{RoundUnitInfo, RoundInfo, TournamentInfo}
import models.team.Team
import models.tournament.GameRules
import modules.ServiceModule
import org.joda.time.DateTime
import org.scalatest.FunSuite
import scaldi.Injector

class GroupTest extends FunSuite {

  implicit val inj = new ServiceModule

  val tournamentInfo = new TournamentInfo("t", None, GameRules(0, 1, 3))
  val roundInfo = new RoundInfo(tournamentInfo)("rName", None)
  val unitInfo = new RoundUnitInfo(roundInfo)("uName", None)

  test("test group contains teams") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val group = new Group("Group A", List(t1, t2, t3, t4))(roundInfo)

    //then
    assert(group.teams == t1 :: t2 :: t3 :: t4 :: Nil)
  }

  test("test group fixtures is default 6") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val group = new Group("Group A", List(t1, t2, t3, t4))(roundInfo)

    //then
    assert(group.fixturesCount == 6)
  }

  test("test matches to play field is default 6") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val group = new Group("Group A", List(t1, t2, t3, t4))(roundInfo)

    //then
    assert(group.matchesToPlay == 6)
  }

  test("test matches to play field equals 4 after two fixtures") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val group = new Group("Group A", List(t1, t2, t3, t4))(roundInfo)

    //when
    val newGroup = group.playFixture(0).playFixture(1)

    //then
    assert(newGroup.matchesToPlay == 4)
  }

  test("group-4 should generate 6 fixtures - home and away matches") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val group = new Group("Group A", List(t1, t2, t3, t4))(roundInfo)

    //then
    assert(group.fixturesCount == 6)

    assert(group.fixtures(0).size == 2)
    assert(group.fixtures(1).size == 2)
    assert(group.fixtures(2).size == 2)
    assert(group.fixtures(3).size == 2)
    assert(group.fixtures(4).size == 2)
    assert(group.fixtures(5).size == 2)

    assert(Set(group.fixtures(0)(0).aTeam, group.fixtures(0)(0).bTeam, group.fixtures(0)(1).aTeam, group.fixtures(0)(1).bTeam).size == 4)
    assert(Set(group.fixtures(1)(0).aTeam, group.fixtures(1)(0).bTeam, group.fixtures(1)(1).aTeam, group.fixtures(1)(1).bTeam).size == 4)
    assert(Set(group.fixtures(2)(0).aTeam, group.fixtures(2)(0).bTeam, group.fixtures(2)(1).aTeam, group.fixtures(2)(1).bTeam).size == 4)
    assert(Set(group.fixtures(3)(0).aTeam, group.fixtures(3)(0).bTeam, group.fixtures(3)(1).aTeam, group.fixtures(3)(1).bTeam).size == 4)
    assert(Set(group.fixtures(4)(0).aTeam, group.fixtures(4)(0).bTeam, group.fixtures(4)(1).aTeam, group.fixtures(4)(1).bTeam).size == 4)
    assert(Set(group.fixtures(5)(0).aTeam, group.fixtures(5)(0).bTeam, group.fixtures(5)(1).aTeam, group.fixtures(5)(1).bTeam).size == 4)
  }

  test("group-3 should generate 6 fixtures - home and away matches") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val group = new Group("Group A", List(t1, t2, t3))(roundInfo)

    //then
    assert(group.fixturesCount == 6)
    assert(group.fixtures(0).size == 1)
    assert(group.fixtures(1).size == 1)
    assert(group.fixtures(2).size == 1)
    assert(group.fixtures(3).size == 1)
    assert(group.fixtures(4).size == 1)
    assert(group.fixtures(5).size == 1)
  }

  test("test teams default results") {
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val group = new Group("Group A", List(t1, t2, t3, t4))(roundInfo)

    assert(group.results.size == 4)

    assert(group.results(0).team.rankPoints == 1)
    assert(group.results(0).goalsConceded == 0)
    assert(group.results(0).goalsScored == 0)
    assert(group.results(0).points == 0)

    assert(group.results(3).team.rankPoints == 4)
    assert(group.results(3).goalsConceded == 0)
    assert(group.results(3).goalsScored == 0)
    assert(group.results(3).points == 0)
  }

  test("test results update - one fixture") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val group = new Group("Group A", List(t1, t2, t3, t4))(roundInfo)

    val m1 = PlayedMatch(t1, t2, Draw(1), Some(DateTime.now()))(unitInfo)
    val m2 = PlayedMatch(t3, t4, WinA(2, 0), Some(DateTime.now()))(unitInfo)
    val f: Fixture = List(m1, m2)

    //when
    val updatedResults = group.updatedFixtureResults(f)

    //then
    assert(updatedResults.size == 4)

    assert(updatedResults(0).team.value == 1)
    assert(updatedResults(0).points == 1)
    assert(updatedResults(0).goalsScored == 1)
    assert(updatedResults(0).goalsConceded == 1)

    assert(updatedResults(1).team.value == 2)
    assert(updatedResults(1).points == 1)
    assert(updatedResults(1).goalsScored == 1)
    assert(updatedResults(1).goalsConceded == 1)

    assert(updatedResults(2).team.value == 3)
    assert(updatedResults(2).points == 3)
    assert(updatedResults(2).goalsScored == 2)
    assert(updatedResults(2).goalsConceded == 0)

    assert(updatedResults(3).team.value == 4)
    assert(updatedResults(3).points == 0)
    assert(updatedResults(3).goalsScored == 0)
    assert(updatedResults(3).goalsConceded == 2)
  }

  test("playing fixture test - first fixture - integration test") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val group = new Group("Group B", List(t1, t2, t3, t4))(roundInfo)

    //when
    val playedUnit = group.playFixture(0)

    //then
    assert(playedUnit.teams.size == 4)
    assert(playedUnit.teams(0) == t1)
    assert(playedUnit.teams(1) == t2)
    assert(playedUnit.teams(2) == t3)
    assert(playedUnit.teams(3) == t4)

    assert(playedUnit.fixturesCount == 6)

    assert(playedUnit.fixtures(0).size == 2)
    assert(playedUnit.fixtures(0)(0).isInstanceOf[PlayedMatch])
    assert(playedUnit.fixtures(0)(0).aTeam == t3)
    assert(playedUnit.fixtures(0)(0).bTeam == t4)
    assert(playedUnit.fixtures(0)(1).isInstanceOf[PlayedMatch])
    assert(playedUnit.fixtures(0)(1).aTeam == t1)
    assert(playedUnit.fixtures(0)(1).bTeam == t2)

    assert(playedUnit.fixtures(1).size == 2)
    assert(!playedUnit.fixtures(1)(0).isInstanceOf[PlayedMatch])
    assert(!playedUnit.fixtures(1)(1).isInstanceOf[PlayedMatch])

    assert(playedUnit.results.size == 4)
    assert(playedUnit.results(0).points + playedUnit.results(1).points + playedUnit.results(2).points + playedUnit.results(3).points <= 6)
    assert(playedUnit.results(0).points + playedUnit.results(1).points + playedUnit.results(2).points + playedUnit.results(3).points >= 4)
  }

  test("playing fixture test - two fixtures - integration test") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val t3 = new Team(3, 3, 3)
    val t4 = new Team(4, 4, 4)
    val group = new Group("Group B", List(t1, t2, t3, t4))(roundInfo)

    //when
    val playedUnit = group.playFixture(0).playFixture(1)

    //then
    assert(playedUnit.teams.size == 4)
    assert(playedUnit.teams(0) == t1)
    assert(playedUnit.teams(1) == t2)
    assert(playedUnit.teams(2) == t3)
    assert(playedUnit.teams(3) == t4)

    assert(playedUnit.fixturesCount == 6)

    assert(playedUnit.fixtures(0).size == 2)
    assert(playedUnit.fixtures(0)(0).isInstanceOf[PlayedMatch])
    assert(playedUnit.fixtures(0)(0).aTeam == t3)
    assert(playedUnit.fixtures(0)(0).bTeam == t4)
    assert(playedUnit.fixtures(0)(1).isInstanceOf[PlayedMatch])
    assert(playedUnit.fixtures(0)(1).aTeam == t1)
    assert(playedUnit.fixtures(0)(1).bTeam == t2)

    assert(playedUnit.fixtures(1).size == 2)
    assert(playedUnit.fixtures(1)(0).isInstanceOf[PlayedMatch])
    assert(playedUnit.fixtures(1)(0).aTeam == t4)
    assert(playedUnit.fixtures(1)(0).bTeam == t1)
    assert(playedUnit.fixtures(1)(1).isInstanceOf[PlayedMatch])
    assert(playedUnit.fixtures(1)(1).aTeam == t2)
    assert(playedUnit.fixtures(1)(1).bTeam == t3)

    assert(playedUnit.results.size == 4)
    assert(playedUnit.results(0).points + playedUnit.results(1).points + playedUnit.results(2).points + playedUnit.results(3).points <= 12)
    assert(playedUnit.results(0).points + playedUnit.results(1).points + playedUnit.results(2).points + playedUnit.results(3).points >= 8)
  }
}
