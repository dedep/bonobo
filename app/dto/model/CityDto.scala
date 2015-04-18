package dto.model

import models.Common.Id
import models.territory.{City, Territory}
import play.api.libs.json.{JsValue, Json}

//todo: rozpatrzyć Format[A]
case class CityDto(id: Option[Id], name: String, population: Int, latitude: Double, longitude: Double,
                   territory: ContainerDto, points: Int, modifiable: Boolean) extends JsonDto[City] {
  private implicit val containerFormat = Json.format[ContainerDto]
  private implicit val format = Json.format[CityDto]

  override val toJson: JsValue = format.writes(this)

  override val toObject: City = {
    val container = new Territory(Some(territory.id), territory.code, territory.name, 0, None, false, false)
    new City(id, name, population, points, container, latitude, longitude)
  }
}

