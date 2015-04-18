package dto.mapper

import dto.model.{ContainerDto, CityDto}
import models.territory.City
import play.api.data.Form
import play.api.data.Forms._
import FormFieldImplicits._

class CityDtoMapper extends BaseDtoMapper[City] {
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