package models.reverse

class RoundUnitInfo(roundInfo: RoundInfo)(val unitName: String, val unitId: Option[Long])
  extends RoundInfo(new TournamentInfo(roundInfo.tournamentName, roundInfo.tournamentId, roundInfo.rules))(roundInfo.roundName, roundInfo.roundId)