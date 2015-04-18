package dto.mapper

import dto.model.JsonDto
import play.api.data.Form

trait BaseDtoMapper[A] {
  val form: Form[_ <: JsonDto[A]]
  def parse(t: A): JsonDto[A]
}
