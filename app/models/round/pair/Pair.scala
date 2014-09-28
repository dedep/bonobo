package models.round.pair

import models.Common
import Common._
import models._match.Match
import models.round.RoundUnit
import models.round.result.TeamResult
import models.team.Team

class Pair(override val name: String, teamsCbn: => List[Team], fixturesCbn: => List[Fixture] = Nil, resultsCbn: => List[TeamResult] = Nil,
            override val id: Option[Long] = None) extends RoundUnit {
  require(teams.size == 2, "Pair size must equal 2")

  def this(name: String, p: (Team, Team)) = this(name, List(p._1, p._2))

  override lazy val teams = teamsCbn

  override lazy val results = if (resultsCbn.nonEmpty) resultsCbn else defaultResults

  override lazy val fixtures = if (fixturesCbn.nonEmpty) fixturesCbn else
    List(List(new Match(teams(0), teams(1))), List(new Match(teams(1), teams(0))))
}