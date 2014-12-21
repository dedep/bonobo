package models.reverse

import models.tournament.GameRules

class TournamentInfo(val tournamentName: String, val tournamentId: Option[Long], val rules: GameRules)
