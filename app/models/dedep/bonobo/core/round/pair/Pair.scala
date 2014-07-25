package models.dedep.bonobo.core.round.pair

import models.dedep.bonobo.core._match.Match
import models.dedep.bonobo.core.round.RoundUnit
import models.dedep.bonobo.core.team.Team

case class Pair(p: (Team, Team)) extends RoundUnit {
  override val fixtures = List(List(new Match(p._1, p._2)), List(new Match(p._2, p._1)))

  override val teams = List(p._1, p._2)
}

