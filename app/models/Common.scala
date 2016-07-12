package models

import models._match.Match
import models.territory.City
import models.unit.TeamResultsOrdering

object Common {
  type Pot = List[City]
  type Fixture = List[Match]
  type Id = Long

  implicit val teamResultsOrdering = TeamResultsOrdering
}