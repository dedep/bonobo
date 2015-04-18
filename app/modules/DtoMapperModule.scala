package modules

import dto.mapper.{TerritoryDtoMapper, CityDtoMapper}
import scaldi.Module

class DtoMapperModule extends Module {
  binding to new CityDtoMapper
  binding to new TerritoryDtoMapper
}
