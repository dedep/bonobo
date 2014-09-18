package models.core.round

import com.typesafe.scalalogging.slf4j.Logger
import models.core.Common
import Common._
import models.core._match.PlayedMatch
import models.core.round.promotion.PromotionsStrategy
import models.core.round.result.{PointsGrantingStrategy, TeamResult}
import models.core.team.Team
import org.slf4j.LoggerFactory

import scala.util.Sorting

trait RoundUnit {
  val fixtures: List[Fixture]
  val results: List[TeamResult]
  val teams: List[Team]
  val id: Option[Long]
  val name: String

  lazy val fixturesCount: Int = fixtures.size
  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  def evalResults: List[TeamResult] = teams.map(TeamResult(_))

  def playFixture(fixtureNum: Int)(implicit strategy: PromotionsStrategy): RoundUnit = {
    require(fixtureNum < fixturesCount)

    val updatedFixture = fixtures(fixtureNum).map(m => PlayedMatch(m.aTeam, m.bTeam, m.eval, m.id))
    val updatedFixtures = fixtures.updated(fixtureNum, updatedFixture)

    val updatedResults: List[TeamResult] = updatedFixtureResults(updatedFixture)

    val parentTeams = teams

    try {
      this.getClass
        .getConstructor(classOf[String], classOf[() => List[Team]], classOf[() => List[Fixture]], classOf[() => List[TeamResult]], classOf[Option[Long]])
        .newInstance(name, () => parentTeams, () => updatedFixtures, () => updatedResults, id)
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
