package controllers

import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory
import play.api.Play
import play.api.db.slick._
import play.api.mvc._
import utils.FunLogger._

//todo: może dać logowanie requestów?
trait BaseController extends Controller {
  private implicit lazy val log = Logger(LoggerFactory.getLogger(this.getClass))

  protected val corsHeaders: List[(String, String)] = Map(
    "Access-Control-Allow-Origin" -> Play.current.configuration.getString("web.url").getOrElse(""),
    "Access-Control-Allow-Headers" -> "Accept, Content-Type"
  ).toList

  protected def performTransactionalDBRequest(requestHandler: DBSessionRequest[AnyContent] => Result) =
    performDBRequest { implicit rs =>
      rs.dbSession.withTransaction {
        requestHandler.apply(rs)
      }
    }

  protected def performDBRequest(requestHandler: DBSessionRequest[AnyContent] => Result) = DBAction {
    rs: DBSessionRequest[AnyContent] => {
      try {
        requestHandler(rs)
          .withHeaders(corsHeaders:_*)
      } catch {
        case e: Exception =>
          InternalServerError("There was an internal error during request.")
            .log(x => "Error occurred during processing DB Request." + e).error()
            .withHeaders(corsHeaders:_*)
      }
    }
  }
}
