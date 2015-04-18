package dto.model

import play.api.libs.json.JsValue

trait JsonDto[A] {
  val toJson: JsValue
  val toObject: A
}