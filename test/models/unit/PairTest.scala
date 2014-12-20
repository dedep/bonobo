package models.unit

import models.Common._
import models._match.PlayedMatch
import models._match.result.{Draw, WinA}
import models.team.Team
import modules.ServiceModule
import org.joda.time.DateTime
import org.scalatest.FunSuite

class PairTest extends FunSuite {

  implicit val inj = new ServiceModule

  test("test teams") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val pair = new Pair("Pair A", (t1, t2))

    //then
    assert(pair.teams == t1 :: t2 :: Nil)
  }

  test("test pair fixtures count") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val pair = new Pair("Pair A", (t1, t2))

    //then
    assert(pair.fixturesCount == 2)
  }

  test("test pair fixtures") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val pair = new Pair("Pair A", (t1, t2))

    //then
    assert(pair.fixtures(0).head.aTeam == t1)
    assert(pair.fixtures(0).head.bTeam == t2)

    assert(pair.fixtures(1).head.aTeam == t2)
    assert(pair.fixtures(1).head.bTeam == t1)
  }

  test("test default results") {
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val pair = new Pair("Pair A", (t1, t2))

    assert(pair.results.size == 2)

    assert(pair.results(0).team.rankPoints == 1)
    assert(pair.results(0).goalsConceded == 0)
    assert(pair.results(0).goalsScored == 0)
    assert(pair.results(0).points == 0)

    assert(pair.results(1).team.rankPoints == 2)
    assert(pair.results(1).goalsConceded == 0)
    assert(pair.results(1).goalsScored == 0)
    assert(pair.results(1).points == 0)
  }

  test("test results update - one doubled fixture") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val pair = new Pair("Pair A", (t1, t2))

    val m1 = PlayedMatch(t1, t2, Draw(1), Some(DateTime.now()))
    val m2 = PlayedMatch(t2, t1, WinA(2, 0), Some(DateTime.now()))
    val f: Fixture = List(m1, m2)

    //when
    val updatedResults = pair.updatedFixtureResults(f)

    //then
    assert(updatedResults.size == 2)

    assert(updatedResults(0).team.value == 1)
    assert(updatedResults(0).points == 1)
    assert(updatedResults(0).goalsScored == 1)
    assert(updatedResults(0).goalsConceded == 3)

    assert(updatedResults(1).team.value == 2)
    assert(updatedResults(1).points == 4)
    assert(updatedResults(1).goalsScored == 3)
    assert(updatedResults(1).goalsConceded == 1)
  }

  test("playing fixture test - first fixture") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val pair = new Pair("Group A", (t1, t2))

    //when
    val playedUnit = pair.playFixture(0)

    //then
    assert(playedUnit.teams.size == 2)
    assert(playedUnit.teams(0) == t1)
    assert(playedUnit.teams(1) == t2)

    assert(playedUnit.fixturesCount == 2)
    assert(playedUnit.fixtures(0).size == 1)
    assert(playedUnit.fixtures(0)(0).isInstanceOf[PlayedMatch])
    assert(playedUnit.fixtures(0)(0).aTeam == t1)
    assert(playedUnit.fixtures(0)(0).bTeam == t2)
    assert(playedUnit.fixtures(1).size == 1)
    assert(!playedUnit.fixtures(1)(0).isInstanceOf[PlayedMatch])
    assert(playedUnit.fixtures(1)(0).aTeam == t2)
    assert(playedUnit.fixtures(1)(0).bTeam == t1)

    assert(playedUnit.results.size == 2)
    assert(playedUnit.results(0).points + playedUnit.results(1).points <= 3)
    assert(playedUnit.results(0).points + playedUnit.results(1).points >= 2)
  }

  test("playing fixture test - two fixtures") {
    //given
    val t1 = new Team(1, 1, 1)
    val t2 = new Team(2, 2, 2)
    val pair = new Pair("Group A", (t1, t2))

    //when
    val playedUnit = pair.playFixture(0).playFixture(1)

    //then
    assert(playedUnit.teams.size == 2)
    assert(playedUnit.teams(0) == t1)
    assert(playedUnit.teams(1) == t2)

    assert(playedUnit.fixturesCount == 2)
    assert(playedUnit.fixtures(0).size == 1)
    assert(playedUnit.fixtures(0)(0).isInstanceOf[PlayedMatch])
    assert(playedUnit.fixtures(0)(0).aTeam == t1)
    assert(playedUnit.fixtures(0)(0).bTeam == t2)
    assert(playedUnit.fixtures(1).size == 1)
    assert(playedUnit.fixtures(1)(0).isInstanceOf[PlayedMatch])
    assert(playedUnit.fixtures(1)(0).aTeam == t2)
    assert(playedUnit.fixtures(1)(0).bTeam == t1)

    assert(playedUnit.results.size == 2)
    assert(playedUnit.results(0).points + playedUnit.results(1).points <= 6)
    assert(playedUnit.results(0).points + playedUnit.results(1).points >= 4)
  }
}
