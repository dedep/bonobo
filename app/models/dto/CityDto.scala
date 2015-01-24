package models.dto

import models.territory.{Territory, City}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{Json, JsValue}

import FormFieldImplicits._

case class CityDto(id: Long, name: String, population: Int, latitude: Double, longitude: Double,
                   territory: ContainerDto, points: Int, modifiable: Boolean) extends JsonDto {
  private implicit val containerFormat = Json.format[ContainerDto]
  private implicit val cityFormat = Json.format[CityDto]

  override val toJson: JsValue = cityFormat.writes(this)

  val toCity: City = {
    val container = new Territory(territory.code, territory.name, 0, None, false, false)
    new City(id, name, population, points, container, latitude, longitude)
  }
}

object CityDto {
  def parse(c: City): CityDto = {
    val containerDto = ContainerDto(c.territory.name, c.territory.code)
    CityDto(c.id, c.name, c.population, c.latitude, c.longitude, containerDto, c.points, c.territory.modifiable)
  }

  val form = Form(mapping(
    "id" -> longNumber,
    "name" -> text,
    "population" -> number,
    "latitude" -> of[Double],
    "longitude" -> of[Double],
    "territory" -> ContainerDto.form,
    "points" -> number,
    "modifiable" -> boolean)
    (CityDto.apply)(CityDto.unapply))
}
