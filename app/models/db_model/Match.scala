package models.db_model

import models.core._match.PlayedMatch
import models.core._match.result.{WinB, Draw, WinA}
import models.table.MatchesTable
import play.api.db.slick.Config.driver.simple._

import play.api.db.slick._
import play.api.mvc.AnyContent

import scala.slick.jdbc.JdbcBackend

object Match {
  val ds = TableQuery[MatchesTable]

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[models.core._match.Match] =
    (for (m <- ds if m.id === id) yield m).firstOption match {
      case None => None
      case Some((unitId: Long, fixtureNum: Int, aTeamId: Long, aTeamGoals: Option[Int], bTeamId: Long, bTeamGoals: Option[Int])) =>
        val aCity = City.fromId(aTeamId).get
        val bCity = City.fromId(bTeamId).get

        if (aTeamGoals.isEmpty || bTeamGoals.isEmpty) {
          Some(models.core._match.Match(aCity, bCity, Some(id)))
        } else {
          val goalsDiff = aTeamGoals.get - bTeamGoals.get
          val result =
            if (goalsDiff > 0) WinA(aTeamGoals.get, bTeamGoals.get)
            else if (goalsDiff == 0) Draw(aTeamGoals.get)
            else WinB(aTeamGoals.get, bTeamGoals.get)

          Some(models.core._match.PlayedMatch(aCity, bCity, result, Some(id)))
        }
    }

  def saveOrUpdate(m: models.core._match.Match, fixtureNum: Int, unitId: Long)(implicit rs: JdbcBackend#Session): Option[Long] =
    if (m.id.nonEmpty) update(m, fixtureNum, unitId) else save(m, fixtureNum, unitId)

  //todo: zwracanie None w przypadku fackupu
  private def save(m: models.core._match.Match, fixtureNum: Int, unitId: Long)(implicit rs: JdbcBackend#Session): Option[Long] = {
    val newIndex = (ds returning ds.map(_.id)) += (unitId, fixtureNum, m.aTeam.id,
      m match {
        case playedMatch: PlayedMatch => Some(playedMatch.result.aGoals)
        case _ => None
      },
      m.bTeam.id,
      m match {
        case playedMatch: PlayedMatch => Some(playedMatch.result.bGoals)
        case _ => None
      })

    Some(newIndex)
  }

  //todo: zwracanie None w przypadku fackupu
  private def update(m: models.core._match.Match, fixtureNum: Long, unitId: Long)(implicit rs: JdbcBackend#Session): Option[Long] = { ???
  }
}
