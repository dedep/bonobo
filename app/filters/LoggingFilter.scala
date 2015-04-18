package filters

import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory
import play.api.mvc._
import scala.concurrent.Future
import utils.FunLogger._
import scala.concurrent.ExecutionContext.Implicits.global

object LoggingFilter extends Filter {

  private implicit val log = Logger(LoggerFactory.getLogger("request"))

  override def apply(next: (RequestHeader) => Future[Result])(rh: RequestHeader) = {
    val start = System.currentTimeMillis

    def logTime(result: Result): Result = {
      val time = System.currentTimeMillis - start
      result
        .withHeaders("Request-Time" -> time.toString)
        .plainLog(s"${rh.method} ${rh.uri} took ${time}ms and returned ${result.header.status}").info()
    }

    val resultF = next(rh)
    resultF foreach logTime
    resultF
  }
}
