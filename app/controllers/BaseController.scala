package controllers

import play.api.db.slick._
import play.api.mvc._

trait BaseController extends Controller {
  protected def wrapDBRequest(requestHandler: DBSessionRequest[AnyContent] => Result) = DBAction {
    rs: DBSessionRequest[AnyContent] => {
      try {
        requestHandler(rs)
      } catch {
        case e: Exception => InternalServerError(views.html.error(e.getMessage))
      }
    }
  }
}
