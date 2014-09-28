package controllers

import db_access.dao.round.RoundDao
import models.round.Round
import play.api.db.slick.DBAction
import play.api.mvc.Controller
import scaldi.{Injectable, Injector}

class RoundController(implicit inj: Injector) extends Controller with Injectable {
  private val roundDao = inject[RoundDao]

  def find(id: Long) = DBAction {
    implicit rs =>
      roundDao.fromId(id)(rs.dbSession) match {
        case None => NotFound(views.html.error("Round not found"))
        case Some(r: Round) => Ok(views.html.round(r))
      }
  }
}