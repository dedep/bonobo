package controllers

import db.dao._match.MatchDao
import scaldi.{Injectable, Injector}

class MatchController(implicit inj: Injector) extends BaseController with Injectable {
  private val matchDao = inject[MatchDao]

  def find(id: Long) = wrapDBRequest {
    implicit rs =>
      matchDao.fromId(id)(rs.dbSession) match {
        case None => NotFound(views.html.error("Match not found"))
        case Some(m: models._match.Match) => Ok(views.html.mmatch(m))
      }
  }
}
