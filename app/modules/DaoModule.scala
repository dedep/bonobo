package modules

import db.dao._match.{MatchDao, MatchDaoImpl}
import db.dao.city.{CityDao, CityDaoImpl}
import db.dao.round.{RoundDao, RoundDaoImpl}
import db.dao.territory.{TerritoryDao, TerritoryDaoImpl}
import db.dao.tournament.{TournamentRulesDaoImpl, TournamentRulesDao, TournamentDao, TournamentDaoImpl}
import db.dao.unit.{UnitDao, UnitDaoImpl}
import scaldi.Module

class DaoModule extends Module {
  bind [TerritoryDao] to new TerritoryDaoImpl
  bind [CityDao] to new CityDaoImpl
  bind [MatchDao] to new MatchDaoImpl
  bind [RoundDao] to new RoundDaoImpl
  bind [UnitDao] to new UnitDaoImpl
  bind [TournamentDao] to new TournamentDaoImpl
  bind [TournamentRulesDao] to new TournamentRulesDaoImpl
}