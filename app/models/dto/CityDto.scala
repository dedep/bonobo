package models.dto

import models.territory.City
import play.api.libs.json.{Json, JsValue}

case class CityDto(id: Long, name: String, population: Int, latitude: Double, longitude: Double,
                   territory: ContainerDto, points: Int) extends JsonDto {
  private implicit val containerFormat = Json.format[ContainerDto]
  private implicit val cityFormat = Json.format[CityDto]

  override val toJson: JsValue = cityFormat.writes(this)
}

object CityDto {
  def parse(c: City): CityDto = {
    val containerDto = ContainerDto(c.territory.name, c.territory.code, c.territory.id)
    CityDto(c.id, c.name, c.population, c.latitude, c.longitude, containerDto, c.points)
  }
}

