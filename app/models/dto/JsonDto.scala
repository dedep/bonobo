package models.dto

import play.api.data.Form
import play.api.libs.json.JsValue

trait JsonDto[A] {
  val toJson: JsValue
  val toObject: A
}

trait JsonDtoService[A] {
  val form: Form[_ <: JsonDto[A]]
  def parse(t: A): JsonDto[A]
}