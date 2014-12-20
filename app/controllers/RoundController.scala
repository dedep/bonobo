package controllers

import db.dao.round.RoundDao
import models.round.Round
import play.api.data.Form
import play.api.data.Forms._
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend

class RoundController(implicit inj: Injector) extends BaseController with Injectable {
  private val roundDao = inject[RoundDao]

  def find(id: Long) = wrapDBRequest { implicit rs =>
    findRound(id, "")(rs.dbSession)
  }

  def findWithFilter = wrapDBRequest { implicit rs =>
    case class FindRoundFormData(id: Int, filter: String)

    val findRoundForm = Form(
      mapping(
        "id" -> number,
        "filter" -> text
      )(FindRoundFormData.apply)(FindRoundFormData.unapply)
    )

    findRoundForm.bindFromRequest.fold(
      hasErrors => BadRequest(""),
      success => findRound(success.id, success.filter)(rs.dbSession)
    )
  }

  private def findRound(id: Long, filter: String)(implicit rs: JdbcBackend#Session) = {
      roundDao.fromId(id) match {
        case None => NotFound(views.html.error("Round not found"))
        case Some(r: Round) => Ok(views.html.round(r, filter))
      }
  }
}