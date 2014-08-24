package models.core.round.group

import models.core.Common
import Common._
import models.core._match.Match
import models.core.round.RoundUnit
import models.core.round.result.TeamResult
import models.core.team.Team

class Group(teamsCbn: => List[Team], fixturesCbn: => List[Fixture] = Nil,
            resultsCbn: => List[TeamResult] = Nil, override val id: Option[Long] = None) extends RoundUnit {
  override lazy val results = if (resultsCbn.nonEmpty) resultsCbn else evalResults
  
  override lazy val teams = teamsCbn

  override lazy val fixtures: List[Fixture] = if (fixturesCbn.nonEmpty) fixturesCbn else {
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
          .map(t => new Match(teams(t), teams((t + i) % teams.size)))
          .toList
      )
      .toList
      .flatMap(uniqueMatchesInFixture)
  }
}

