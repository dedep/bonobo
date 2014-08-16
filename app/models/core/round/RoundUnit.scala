package models.core.round

import com.typesafe.scalalogging.slf4j.Logger
import models.core.Common
import Common._
import models.core._match.PlayedMatch
import models.core.round.result.TeamResult
import models.core.team.Team
import org.slf4j.LoggerFactory

trait RoundUnit {
  val fixtures: List[Fixture]
  val results: List[TeamResult]
  val teams: List[Team]

  lazy val fixturesCount: Int = fixtures.size
  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  def evalResults: List[TeamResult] = teams.map(TeamResult(_))

  def playFixture(fixtureNum: Int): RoundUnit = {
    require(fixtureNum < fixturesCount)

    val updatedFixture = fixtures(fixtureNum).map(m => PlayedMatch(m.aTeam, m.bTeam, m.eval))
    val updatedFixtures = fixtures.updated(fixtureNum, updatedFixture)

    val updatedResults = updatedFixtureResults(updatedFixture)

    val parentTeams = teams

    try {
      this.getClass
        .getConstructor(classOf[() => List[Team]], classOf[() => List[Fixture]], classOf[() => List[TeamResult]])
        .newInstance(() => parentTeams, () => updatedFixtures, () => updatedResults)
    } catch {
      case cnfe: ClassNotFoundException => log.error("Cannot find saved in database class", cnfe) ; this
      case cce: ClassCastException => log.error("Cannot cast saved in database class into Round", cce) ; this
    }
  }

  def updatedFixtureResults(fixture: Fixture): List[TeamResult] =
    results
      .map(r => fixture.filter(_.aTeam == r.team).foldLeft(r){_ aPlus _.asInstanceOf[PlayedMatch].result})
      .map(r => fixture.filter(_.bTeam == r.team).foldLeft(r){_ bPlus _.asInstanceOf[PlayedMatch].result})
}
