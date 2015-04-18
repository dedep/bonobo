package models.dto

import models.Common.Id
import models.territory.{Territory, City}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{Json, JsValue}

import FormFieldImplicits._

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

object CityDto extends JsonDtoService[City] {
  override def parse(c: City): CityDto = {
    val containerDto = ContainerDto(c.territory.id.get, c.territory.name, c.territory.code)
    CityDto(c.id, c.name, c.population, c.latitude, c.longitude, containerDto, c.points, c.territory.modifiable)
  }

  override val form: Form[CityDto] = Form(mapping(
    "id" -> optional(longNumber),
    "name" -> text,
    "population" -> number,
    "latitude" -> of[Double],
    "longitude" -> of[Double],
    "territory" -> ContainerDto.form,
    "points" -> number,
    "modifiable" -> boolean)
    (CityDto.apply)(CityDto.unapply))
}
