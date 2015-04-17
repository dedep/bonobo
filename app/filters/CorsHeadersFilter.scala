package filters

import play.api.Play
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object CorsHeadersFilter extends Filter {
  def apply(f: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] =
    f(rh).map(_.withHeaders(
      "Access-Control-Allow-Origin" -> Play.current.configuration.getString("web.url").getOrElse(""),
      "Access-Control-Allow-Headers" -> "Accept, Content-Type",
      "Access-Control-Allow-Methods" -> "GET, POST, DELETE, OPTIONS"
    ))
}