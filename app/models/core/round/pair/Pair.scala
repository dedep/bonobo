package models.core.round.pair

import models.core.Common._
import models.core._match.Match
import models.core.round.RoundUnit
import models.core.round.result.TeamResult
import models.core.team.Team

class Pair(teamsCbn: => List[Team], fixturesCbn: => List[Fixture] = Nil, resultsCbn: => List[TeamResult] = Nil) extends RoundUnit {
  require(teams.size == 2, "Pair size must equal 2")

  def this(p: (Team, Team)) = this(List(p._1, p._2))

  override lazy val teams = teamsCbn

  override lazy val results = if (resultsCbn.nonEmpty) resultsCbn else evalResults

  override lazy val fixtures = if (fixturesCbn.nonEmpty) fixturesCbn else
    List(List(new Match(teams(0), teams(1))), List(new Match(teams(1), teams(0))))
}