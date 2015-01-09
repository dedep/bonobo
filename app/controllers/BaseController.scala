package controllers

import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory
import play.api.Play
import play.api.db.slick._
import play.api.mvc._
import utils.FunLogger._

trait BaseController extends Controller {
  private implicit lazy val log = Logger(LoggerFactory.getLogger(this.getClass))

  protected val corsHeaders: List[(String, String)] = Map(
    "Access-Control-Allow-Origin" -> Play.current.configuration.getString("web.url").getOrElse(""),
    "Access-Control-Allow-Headers" -> "Accept, Content-Type"
  ).toList

  protected def serveHttpResponseWithTransactionalDB(requestHandler: DBSessionRequest[AnyContent] => Result) =
    serveHttpResponseWithDB { implicit rs =>
      rs.dbSession.withTransaction {
        requestHandler.apply(rs)
      }
    }

  protected def serveHttpResponseWithDB(requestHandler: DBSessionRequest[AnyContent] => Result) = DBAction {
    rs: DBSessionRequest[AnyContent] => {
      prepareFinalResponse(requestHandler(rs))(rs.request)
    }
  }

  protected def serveHttpResponse(block: => Result) = Action { implicit request =>
    prepareFinalResponse(block)(request)
  }
  
  private def prepareFinalResponse(block: => Result)(request: Request[AnyContent]): Result =
    try {
      block.withHeaders(corsHeaders: _*)
        .log(response => {
        "REQUEST: " + request.method + " " + request.path + "\n" +
          "Headers: " + request.headers.toSimpleMap + "\n" +
          "Body: " + request.body + "\n" +
          "RESPONSE: " + response.toString()
      }).info()
    } catch {
      case e: Exception =>
        InternalServerError("There was an internal error during request.")
          .logException(r => "Error occurred during request processing.", e).error()
    }
}
