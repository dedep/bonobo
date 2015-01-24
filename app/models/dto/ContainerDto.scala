package models.dto

import play.api.data.Forms._

case class ContainerDto(name: String, code: String)

object ContainerDto {
  val form = mapping(
    "name" -> text,
    "code" -> text
  )(ContainerDto.apply)(ContainerDto.unapply)
}
