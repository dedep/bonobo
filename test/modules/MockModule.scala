package modules

import db.dao._match.MatchDao
import db.dao.city.CityDao
import db.dao.round.RoundDao
import db.dao.territory.TerritoryDao
import db.dao.tournament.TournamentDao
import db.dao.unit.UnitDao
import org.specs2.mock.Mockito
import scaldi.Module

import DBMock._

class MockModule extends Module with Mockito {
  bind [CityDao] to prepareCityDaoMockData(mock[CityDao])
  bind [TerritoryDao] to prepareTerritoryDaoMockData(mock[TerritoryDao])
  bind [MatchDao] to prepareMatchDaoMockData(mock[MatchDao])
  bind [RoundDao] to prepareRoundDaoMockData(mock[RoundDao])
  bind [UnitDao] to prepareUnitDaoMockData(mock[UnitDao])
  bind [TournamentDao] to mock[TournamentDao]
}
