package models

import models._match.Match
import models.team.Team
import models.unit.TeamResultsOrdering

object Common {
  type Pot = List[Team]
  type Fixture = List[Match]
  type Id = Long

  implicit val teamResultsOrdering = TeamResultsOrdering
}