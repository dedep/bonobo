package modules

import db.row.{TerritoryDBRowService, CityDBRowService}
import scaldi.Module

class RowMapperModule extends Module {
  bind [CityDBRowService] to new CityDBRowService
  bind [TerritoryDBRowService] to new TerritoryDBRowService
}