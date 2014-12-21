package models.reverse

class RoundInfo(tournamentInfo: TournamentInfo)(val roundName: String, val roundId: Option[Long])
  extends TournamentInfo(tournamentInfo.tournamentName, tournamentInfo.tournamentId, tournamentInfo.rules)
