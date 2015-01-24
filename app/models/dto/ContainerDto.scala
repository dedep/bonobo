package models.dto

import play.api.data.Forms._

case class ContainerDto(name: String, code: String, id: Long)

object ContainerDto {
  val form = mapping(
    "name" -> text,
    "code" -> text,
    "id" -> longNumber
  )(ContainerDto.apply)(ContainerDto.unapply)
}
