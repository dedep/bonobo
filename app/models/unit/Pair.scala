package models.unit

import com.typesafe.scalalogging.slf4j.Logger
import models.Common._
import models._match.Match
import models.reverse.RoundInfo
import models.team.Team
import models.tournament.GameRules
import org.slf4j.LoggerFactory
import scaldi.{Injectable, Injector}
import service.game.promoter.PromotionsStrategy

class Pair(override val name: String, teamsCbn: => List[Team], fixturesCbn: => List[Fixture] = Nil, resultsCbn: => List[UnitTeamResult] = Nil,
           override val id: Option[Long] = None, val generateFixtures: Boolean = true,
           promotedTeamsCbn: => List[Team] = Nil, eliminatedTeamsCbn: => List[Team] = Nil)
          (override val roundInfo: RoundInfo)
          (override implicit val inj: Injector)
  extends RoundUnit with Injectable {

  def this(name: String, p: (Team, Team))(r: RoundInfo)(implicit inj: Injector) = {
    this(name, List(p._1, p._2))(r)
  }

  val strategy = inject[PromotionsStrategy]

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  override lazy val teams = teamsCbn
  override lazy val results = if (!generateFixtures) resultsCbn else defaultResults
  override lazy val promotedTeams = promotedTeamsCbn
  override lazy val eliminatedTeams = eliminatedTeamsCbn

  override lazy val fixtures = if (!generateFixtures) fixturesCbn else
    List(List(new Match(teams(0), teams(1))(toRoundUnitInfo)), List(new Match(teams(1), teams(0))(toRoundUnitInfo)))

  override val promotedTeamsSize: Int = 1

  override def playFixture(fixtureNum: Int): RoundUnit = {
    val fixturesResults = playFixtureAndGetResultsAndFixtures(fixtureNum)
    val newResults = fixturesResults._1
    val newFixtures = fixturesResults._2
    val newTeams = teams

    val promotionsAndEliminations =
      strategy.findPromotedAndEliminatedTeams(new Pair(name, newTeams, newFixtures, newResults, id, false)(roundInfo))
    val promotions = promotionsAndEliminations._1
    val eliminations = promotionsAndEliminations._2

    new Pair(name, newTeams, newFixtures, newResults, id, false, promotions, eliminations)(roundInfo)
  }
}