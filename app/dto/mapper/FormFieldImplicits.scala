package dto.mapper

import play.api.data.format.Formatter

object FormFieldImplicits {
  implicit def doubleFormat: Formatter[Double] = new Formatter[Double] {
    def bind(key: String, data: Map[String, String]) = Right(data(key).toDouble)
    def unbind(key: String, value: Double) = Map(key -> value.toString)
  }
}