package dto.model

import models.Common.Id
import models.territory.Territory
import play.api.libs.json._

case class TerritoryDto(id: Option[Id], code: String, name: String, population: Long, parent: Option[ContainerDto],
                        isCountry: Boolean, modifiable: Boolean) extends JsonDto[Territory] {
  private implicit val containerFormat = Json.format[ContainerDto]
  private implicit val format = Json.format[TerritoryDto]

  override val toJson: JsValue = format.writes(this)

  override val toObject: Territory = {
    val parentTerritoryStub = parent.map(p => new Territory(Some(p.id), p.code, "", 0, None, false, false))
    new Territory(id, code, name, population, parentTerritoryStub, isCountry, true)
  }
}
