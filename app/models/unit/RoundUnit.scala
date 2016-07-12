package models.unit

import com.typesafe.scalalogging.slf4j.Logger
import models.Common._
import models._match.PlayedMatch
import models.reverse.{RoundInfo, RoundUnitInfo}
import models.territory.City
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import scaldi.Injector

import scala.util.Sorting

trait RoundUnit {
  val fixtures: List[Fixture]
  val results: List[UnitTeamResult]
  val teams: List[City]
  val id: Option[Long]
  val name: String
  val promotedTeams: List[City]
  val eliminatedTeams: List[City]
  val promotedTeamsSize: Int
  val roundInfo: RoundInfo

  implicit val inj: Injector
  implicit val rules = roundInfo.rules

  private val log = Logger(LoggerFactory.getLogger("app"))

  lazy val fixturesCount: Int = fixtures.size
  lazy val playedFixtures: Int = results(0).matchesAmount
  lazy val matchesToPlay = fixturesCount - playedFixtures
  lazy val defaultResults: List[UnitTeamResult] = teams.map(UnitTeamResult(_))
  lazy val sortedResults = {
    Sorting.stableSort(results.filter(r => promotedTeams.contains(r.team))).reverse ++
    Sorting.stableSort(results.filter(r => !promotedTeams.contains(r.team) && !eliminatedTeams.contains(r.team))).reverse ++
    Sorting.stableSort(results.filter(r => eliminatedTeams.contains(r.team))).reverse
  }

  def playFixture(fixtureNum: Int): RoundUnit
  
  protected def playFixtureAndGetResultsAndFixtures(fixtureNum: Int): (List[UnitTeamResult], List[Fixture]) = {
    require(fixtureNum < fixturesCount, fixtureNum + " must be smaller than " + fixturesCount + "in "
      + this.fixtures.map(f => "SIZE " + fixtures.size + " " + f.mkString(", ")))

    val updatedFixture = fixtures(fixtureNum).map(m =>
      PlayedMatch(m.aTeam, m.bTeam, m.eval(), Some(DateTime.now()), m.id)(toRoundUnitInfo))
    val updatedFixtures = fixtures.updated(fixtureNum, updatedFixture)
    val updatedResults: List[UnitTeamResult] = updatedFixtureResults(updatedFixture)

    (updatedResults, updatedFixtures)
  }

  def updatedFixtureResults(fixture: Fixture): List[UnitTeamResult] = {
    results
      .map(r => fixture.filter(_.aTeam == r.team).foldLeft(r){_ aPlus _.asInstanceOf[PlayedMatch].result})
      .map(r => fixture.filter(_.bTeam == r.team).foldLeft(r){_ bPlus _.asInstanceOf[PlayedMatch].result})
  }

  protected val toRoundUnitInfo: RoundUnitInfo = new RoundUnitInfo(roundInfo)(name, id)
}
