package dto.mapper

import dto.model.{ContainerDto, TerritoryDto}
import models.territory.Territory
import play.api.data.Form
import play.api.data.Forms._

class TerritoryDtoMapper {
  def parse(t: Territory): TerritoryDto = {
    val containerDto = t.container.map(c => ContainerDto(c.id.get, c.name, c.code))
    TerritoryDto(t.id, t.code, t.name, t.population, containerDto, t.isCountry, t.modifiable)
  }

  val form: Form[TerritoryDto] = Form(mapping(
    "id" -> optional(longNumber),
    "code" -> text,
    "name" -> text,
    "population" -> longNumber,
    "parent" -> optional(ContainerDto.form),
    "isCountry" -> boolean,
    "modifiable" -> boolean)
    (TerritoryDto.apply)(TerritoryDto.unapply))
}
