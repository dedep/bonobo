package models.dto

import play.api.libs.json.JsValue

trait JsonDto {
  val toJson: JsValue
}