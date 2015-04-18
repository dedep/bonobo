package modules

import db.row.mapper.{TerritoryRowMapper, CityRowMapper}
import scaldi.Module

class RowMapperModule extends Module {
  bind [CityRowMapper] to new CityRowMapper
  bind [TerritoryRowMapper] to new TerritoryRowMapper
}