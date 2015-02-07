package modules

import controllers.validator.TerritoryValidator
import scaldi.Module

class ValidatorModule extends Module {
  binding to new TerritoryValidator
}
