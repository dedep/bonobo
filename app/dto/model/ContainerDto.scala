package dto.model

import play.api.data.Forms._

case class ContainerDto(id: Long, name: String, code: String)

object ContainerDto {
  val form = mapping(
    "id" -> longNumber,
    "name" -> text,
    "code" -> text
  )(ContainerDto.apply)(ContainerDto.unapply)
}
