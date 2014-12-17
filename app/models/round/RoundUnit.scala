package models.round

import com.typesafe.scalalogging.slf4j.Logger
import models.Common
import Common._
import models._match.PlayedMatch
import models.round.promotion.PromotionsStrategy
import models.round.result.TeamResult
import models.team.Team
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import utils.FunLogger._

import scala.util.Sorting

trait RoundUnit {
  val fixtures: List[Fixture]
  val results: List[TeamResult]
  val teams: List[Team]
  val id: Option[Long]
  val name: String
  val promotedTeams: List[Team]
  val eliminatedTeams: List[Team]
  val promotedTeamsSize: Int

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  lazy val fixturesCount: Int = fixtures.size
  lazy val playedFixtures: Int = results(0).matchesAmount
  lazy val matchesToPlay = fixturesCount - playedFixtures
  lazy val defaultResults: List[TeamResult] = teams.map(TeamResult(_))
  lazy val sortedResults = {
    Sorting.stableSort(results.filter(r => promotedTeams.contains(r.team))).reverse ++
    Sorting.stableSort(results.filter(r => !promotedTeams.contains(r.team) && !eliminatedTeams.contains(r.team))).reverse ++
    Sorting.stableSort(results.filter(r => eliminatedTeams.contains(r.team))).reverse
  }

  def playFixture(fixtureNum: Int)(implicit strategy: PromotionsStrategy): RoundUnit
  
  protected def playFixtureAndGetResultsAndFixtures(fixtureNum: Int): (List[TeamResult], List[Fixture]) = {
    require(fixtureNum < fixturesCount, fixtureNum + " must be smaller than " + fixturesCount + "in "
      + this.fixtures.map(f => "SIZE " + fixtures.size + " " + f.mkString(", ")))

    def updatedFixtureResults(fixture: Fixture): List[TeamResult] = {
      results
        .map(r => fixture.filter(_.aTeam == r.team).foldLeft(r){_ aPlus _.asInstanceOf[PlayedMatch].result})
        .map(r => fixture.filter(_.bTeam == r.team).foldLeft(r){_ bPlus _.asInstanceOf[PlayedMatch].result})
    }

    val updatedFixture = fixtures(fixtureNum).map(m => PlayedMatch(m.aTeam, m.bTeam, m.eval, Some(DateTime.now()), m.id))
    val updatedFixtures = fixtures.updated(fixtureNum, updatedFixture)
    val updatedResults: List[TeamResult] = updatedFixtureResults(updatedFixture)

    (updatedResults, updatedFixtures)
  }
}
