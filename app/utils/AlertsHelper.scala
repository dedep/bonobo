package utils

object AlertsHelper {

  type Alert = Option[Either[String, String]]

  def success(msg: String): Alert = Some(Right(msg))

  def fail(msg: String): Alert = Some(Left(msg))

  val none = None
}
