package models.unit

import models.Common._
import models._match.Match
import models.reverse.RoundInfo
import models.team.Team
import models.tournament.GameRules
import scaldi.{Injectable, Injector}
import service.game.promoter.PromotionsStrategy

class Group(override val name: String, teamsCbn: => List[Team], fixturesCbn: => List[Fixture] = Nil, resultsCbn: => List[UnitTeamResult] = Nil,
            override val id: Option[Long] = None, val generateFixtures: Boolean = true,
            promotedTeamsCbn: => List[Team] = Nil, eliminatedTeamsCbn: => List[Team] = Nil)
            (override val roundInfo: RoundInfo)
            (override implicit val inj: Injector)
  extends RoundUnit with Injectable {

  val strategy = inject[PromotionsStrategy]

  override lazy val results = if (generateFixtures) defaultResults else resultsCbn
  override lazy val teams = teamsCbn
  override lazy val promotedTeams = promotedTeamsCbn
  override lazy val eliminatedTeams = eliminatedTeamsCbn

  override lazy val fixtures: List[Fixture] = if (!generateFixtures) fixturesCbn else {
    def uniqueMatchesInFixture(fix: Fixture): List[Fixture] = {
      fix.foldLeft(List.empty[Fixture]) { (p, c) => {

        def findTeamVacancyInFixtures: Option[Fixture] =
          p.filterNot(_.exists(m => m.aTeam == c.aTeam || m.aTeam == c.bTeam || m.bTeam == c.aTeam || m.bTeam == c.bTeam)).headOption

        findTeamVacancyInFixtures match {
          case Some(f: Fixture) => p.updated(p.indexOf(f), c :: f)
          case None => p ++ List(List(c))
        }
      }
      }
    }

    (1 until teams.size)
      .map(
        i => (0 until teams.size)
          .map(t => new Match(teams(t), teams((t + i) % teams.size))(toRoundUnitInfo))
          .toList
      )
      .toList
      .flatMap(uniqueMatchesInFixture)
  }

  override def playFixture(fixtureNum: Int): RoundUnit = {
    val fixturesResults = playFixtureAndGetResultsAndFixtures(fixtureNum)
    val newResults = fixturesResults._1
    val newFixtures = fixturesResults._2
    val newTeams = teams

    val promotionsAndEliminations =
      strategy.findPromotedAndEliminatedTeams(new Group(name, newTeams, newFixtures, newResults, id, false)(roundInfo))
    val promotions = promotionsAndEliminations._1
    val eliminations = promotionsAndEliminations._2

    new Group(name, newTeams, newFixtures, newResults, id, false, promotions, eliminations)(roundInfo)
  }

  override val promotedTeamsSize: Int = 2
}

