package modules

import db_access.dao._match.{MatchDaoImpl, MatchDao}
import db_access.dao.city.{CityDaoImpl, CityDao}
import db_access.dao.round.{RoundDaoImpl, RoundDao}
import db_access.dao.territory.{TerritoryDaoImpl, TerritoryDao}
import db_access.dao.tournament.{TournamentDaoImpl, TournamentDao}
import db_access.dao.unit.{UnitDao, UnitDaoImpl}

import scaldi.Module

class DaoModule extends Module {
  bind [TerritoryDao] to new TerritoryDaoImpl
  bind [CityDao] to new CityDaoImpl
  bind [MatchDao] to new MatchDaoImpl
  bind [RoundDao] to new RoundDaoImpl
  bind [UnitDao] to new UnitDaoImpl
  bind [TournamentDao] to new TournamentDaoImpl
}