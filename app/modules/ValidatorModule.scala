package modules

import controllers.validator.{CityValidator, TerritoryValidator}
import scaldi.Module

class ValidatorModule extends Module {
  binding to new TerritoryValidator
  binding to new CityValidator
}
