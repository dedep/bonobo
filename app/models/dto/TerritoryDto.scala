package models.dto

import models.territory.Territory
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._

case class TerritoryDto(id: Long, code: String, name: String, population: Long, parent: Option[ContainerDto],
                        isCountry: Boolean, modifiable: Boolean) extends JsonDto {
  private implicit val containerFormat = Json.format[ContainerDto]
  private implicit val territoryFormat = Json.format[TerritoryDto]

  val toJson: JsValue = territoryFormat.writes(this)
}

object TerritoryDto {
  def parse(t: Territory): TerritoryDto = {
    val containerDto = t.container.map(c => ContainerDto(c.name, c.code, c.id))
    TerritoryDto(t.id, t.code, t.name, t.population, containerDto, t.isCountry, t.modifiable)
  }

  val form = Form(mapping(
    "id" -> longNumber,
    "code" -> text,
    "name" -> text,
    "population" -> longNumber,
    "parent" -> optional(ContainerDto.form),
    "isCountry" -> boolean,
    "modifiable" -> boolean)
    (TerritoryDto.apply)(TerritoryDto.unapply))
}
