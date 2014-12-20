package models

import models._match.Match
import models.team.Team
import models.unit.TeamResultsOrdering
import service.game.manager.FootballGameManager

object Common {
  type Pot = List[Team]
  type Fixture = List[Match]

  implicit val teamResultsOrdering = TeamResultsOrdering
  implicit val promotionsStrategy = new FootballGameManager
}