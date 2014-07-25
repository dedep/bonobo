package models.dedep.bonobo.core.round

import models.dedep.bonobo.core.Common
import Common._
import models.dedep.bonobo.core._match.PlayedMatch
import models.dedep.bonobo.core.round.result.TeamResult
import models.dedep.bonobo.core.team.Team

trait RoundUnit {
  val fixtures: List[Fixture]
  lazy val results: List[TeamResult] = teams.map(TeamResult(_))

  val teams: List[Team]
  lazy val fixturesCount: Int = fixtures.size

  def playFixture(fixtureNum: Int): RoundUnit = {
    require(fixtureNum < fixturesCount)

    val updatedFixture = fixtures(fixtureNum).map(m => PlayedMatch(m.aTeam, m.bTeam, m.eval))
    val updatedFixtures = fixtures.updated(fixtureNum, updatedFixture)

    val updatedResults = updatedFixtureResults(updatedFixture)

    val parentTeams = teams

    //todo: przemśleć to jeszcze
    new RoundUnit {
      override val teams: List[Team] = parentTeams
      override val fixtures: List[Fixture] = updatedFixtures
      override lazy val results: List[TeamResult] = updatedResults
    }
  }

  def updatedFixtureResults(fixture: Fixture): List[TeamResult] =
    results
      .map(r => fixture.filter(_.aTeam == r.team).foldLeft(r){_ aPlus _.asInstanceOf[PlayedMatch].result})
      .map(r => fixture.filter(_.bTeam == r.team).foldLeft(r){_ bPlus _.asInstanceOf[PlayedMatch].result})
}
