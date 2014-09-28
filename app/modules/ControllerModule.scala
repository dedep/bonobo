package modules

import controllers._
import scaldi.Module

class ControllerModule extends Module {
  binding to new AppController
  binding to new CityController
  binding to new MatchController
  binding to new RoundController
  binding to new TerritoryController
  binding to new TournamentController
  binding to new UnitController
}
