package models.dto

import models.territory.Territory
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._

case class TerritoryDto(code: String, name: String, population: Long, parent: Option[ContainerDto],
                        isCountry: Boolean, modifiable: Boolean) extends JsonDto[Territory] {
  private implicit val containerFormat = Json.format[ContainerDto]
  private implicit val format = Json.format[TerritoryDto]

  override val toJson: JsValue = format.writes(this)

  override val toObject: Territory = {
    val parentTerritoryStub = parent.map(p => new Territory(p.code, "", 0, None, false, false))
    new Territory(code, name, population, parentTerritoryStub, isCountry, true)
  }
}

object TerritoryDto extends JsonDtoService[Territory] {
  override def parse(t: Territory): TerritoryDto = {
    val containerDto = t.container.map(c => ContainerDto(c.name, c.code))
    TerritoryDto(t.code, t.name, t.population, containerDto, t.isCountry, t.modifiable)
  }

  override val form: Form[TerritoryDto] = Form(mapping(
    "code" -> text,
    "name" -> text,
    "population" -> longNumber,
    "parent" -> optional(ContainerDto.form),
    "isCountry" -> boolean,
    "modifiable" -> boolean)
    (TerritoryDto.apply)(TerritoryDto.unapply))
}
