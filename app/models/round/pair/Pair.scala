package models.round.pair

import com.typesafe.scalalogging.slf4j.Logger
import models.Common
import Common._
import models._match.Match
import models.round.RoundUnit
import models.round.promotion.PromotionsStrategy
import models.round.result.TeamResult
import models.team.Team
import org.slf4j.LoggerFactory

class Pair(override val name: String, teamsCbn: => List[Team], fixturesCbn: => List[Fixture] = Nil, resultsCbn: => List[TeamResult] = Nil,
            override val id: Option[Long] = None, val generateFixtures: Boolean = true,
            promotedTeamsCbn: => List[Team] = Nil, eliminatedTeamsCbn: => List[Team] = Nil) extends RoundUnit {

  def this(name: String, p: (Team, Team)) = this(name, List(p._1, p._2))

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  override lazy val teams = teamsCbn
  override lazy val results = if (!generateFixtures) resultsCbn else defaultResults
  override lazy val promotedTeams = promotedTeamsCbn
  override lazy val eliminatedTeams = eliminatedTeamsCbn

  override lazy val fixtures = if (!generateFixtures) fixturesCbn else
    List(List(new Match(teams(0), teams(1))), List(new Match(teams(1), teams(0))))

  override val promotedTeamsSize: Int = 1

  override def playFixture(fixtureNum: Int)(implicit strategy: PromotionsStrategy): RoundUnit = {
    val fixturesResults = playFixtureAndGetResultsAndFixtures(fixtureNum)
    val newResults = fixturesResults._1
    val newFixtures = fixturesResults._2
    val newTeams = teams

    val promotionsAndEliminations =
      strategy.findPromotedAndEliminatedTeams(new Pair(name, newTeams, newFixtures, newResults, id, false))
    val promotions = promotionsAndEliminations._1
    val eliminations = promotionsAndEliminations._2

    new Pair(name, newTeams, newFixtures, newResults, id, false, promotions, eliminations)
  }
}