package models.db_model

import models.core._match.result.{WinB, Draw, WinA}
import models.table.MatchesTable
import play.api.db.slick.Config.driver.simple._

import play.api.db.slick._
import play.api.mvc.AnyContent

object Match {
  val ds = TableQuery[MatchesTable]
  val autoIncInsert = ds.map(e => ()) returning ds.map(_.id)

  def fromId(id: Long)(implicit rs: DBSessionRequest[AnyContent]): Option[models.core._match.Match] =
    (for (m <- ds if m.id === id) yield m).firstOption match {
      case None => None
      case Some((id: Long, unitId: Long, fixtureNum: Int, aTeamId: Long, aTeamGoals: Option[Int], bTeamId: Long, bTeamGoals: Option[Int])) =>
        val aCity = City.fromId(aTeamId).get
        val bCity = City.fromId(bTeamId).get

        if (aTeamGoals.isEmpty || bTeamGoals.isEmpty) {
          Some(models.core._match.Match(aCity, bCity))
        } else {
          val goalsDiff = aTeamGoals.get - bTeamGoals.get
          val result =
            if (goalsDiff > 0) WinA(aTeamGoals.get, bTeamGoals.get)
            else if (goalsDiff == 0) Draw(aTeamGoals.get)
            else WinB(aTeamGoals.get, bTeamGoals.get)

          Some(models.core._match.PlayedMatch(aCity, bCity, result))
        }
    }
}
