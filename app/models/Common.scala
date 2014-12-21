package models

import models._match.Match
import models.team.Team
import models.tournament.GameRules
import models.unit.TeamResultsOrdering

object Common {
  type Pot = List[Team]
  type Fixture = List[Match]

  implicit val teamResultsOrdering = TeamResultsOrdering
}