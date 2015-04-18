package modules

import db.dao._
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