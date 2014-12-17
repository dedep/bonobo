package controllers

import com.typesafe.scalalogging.slf4j.Logger
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import play.api.db.slick._
import play.api.mvc._

//todo: może dać logowanie requestów?
trait BaseController extends Controller {
  private lazy val log = Logger(LoggerFactory.getLogger(this.getClass))
  protected def wrapDBRequest(requestHandler: DBSessionRequest[AnyContent] => Result) = DBAction {
    rs: DBSessionRequest[AnyContent] => {
      try {
        requestHandler(rs)
      } catch {
        case e: Exception => {
          log.error("Error occurred during processing DB Request " + e.getMessage + "\n" + ExceptionUtils.getStackTrace(e))
          InternalServerError(views.html.error(e.getMessage))
        }
      }
    }
  }
}
