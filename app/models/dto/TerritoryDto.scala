package models.dto

import models.territory.Territory
import play.api.libs.json._

case class ContainerDto(name: String, code: String)

case class TerritoryDto(id: Long, code: String, name: String, population: Long, parent: Option[ContainerDto],
                        isCountry: Boolean, modifiable: Boolean) {
  private implicit val containerFormat = Json.format[ContainerDto]
  private implicit val territoryFormat = Json.format[TerritoryDto]

  val toJson: JsValue = territoryFormat.writes(this)
}

object TerritoryDto {
  def parse(t: Territory): TerritoryDto = {
    val containerDto = t.container.map(c => ContainerDto(c.name, c.code))
    TerritoryDto(t.id, t.code, t.name, t.population, containerDto, t.isCountry, t.modifiable)
  }
}
