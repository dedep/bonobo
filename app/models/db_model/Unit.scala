package models.db_model

import models.core.Common.Fixture
import models.core.round.RoundUnit
import models.core.round.result.TeamResult
import models.core.team.Team
import models.table.{UnitsCitiesTable, UnitsTable}
import play.api.db.slick.Config.driver.simple._

import play.api.db.slick._
import play.api.mvc.AnyContent

object Unit {
  val ds = TableQuery[UnitsTable]
  val citiesDs = TableQuery[UnitsCitiesTable]

  def fromId(id: Long)(implicit rs: DBSessionRequest[AnyContent]): Option[RoundUnit] =
    (for (m <- ds if m.id === id) yield m).firstOption match {
      case None => None
      case Some((roundId: Long, clazz: String)) => {
        val fixtures =
          groupMatchesByFixture(Match.ds.filter(_.unitId === id).map(m => (m.id, m.fixtureNum)).list.map(a => (Match.fromId(a._1).get, a._2)))

        val citiesAndResults = citiesDs.filter(_.unitId === id).list.map(a => {
          val city = City.fromId(a._1).get
          val result = TeamResult(city, a._3, a._4, a._5)

          (city, result)
        })

        val cities = citiesAndResults.map(_._1)
        val results = citiesAndResults.map(_._2)

        Some(Class.forName(clazz)
          .getConstructor(classOf[() => List[Team]], classOf[() => List[Fixture]], classOf[() => List[TeamResult]], classOf[Option[Long]])
          .newInstance(() => cities, () => fixtures, () => results, Some(id))
          .asInstanceOf[RoundUnit])
      }
    }

  private def groupMatchesByFixture(matchFixtureList: List[(models.core._match.Match, Int)]): List[Fixture] =
    matchFixtureList.groupBy(_._2).toList.map(_._2.map(_._1))

  def saveOrUpdate(u: RoundUnit, parentRoundId: Long)(implicit rs: DBSessionRequest[AnyContent]): Option[Long] =
    if (u.id.nonEmpty) update(u, parentRoundId) else save(u, parentRoundId)

  //todo: zwracanie None w przypadku fackupu
  private def save(u: RoundUnit, parentRoundId: Long)(implicit rs: DBSessionRequest[AnyContent]): Option[Long] = {
    val newIndex = (ds returning ds.map(_.id)) += (parentRoundId, u.getClass.getName)

    citiesDs ++= u.teams.map(team => {
      val teamResult = u.results.filter(_.team == team).head
      (team.id, newIndex, teamResult.points, teamResult.goalsScored, teamResult.goalsConceded)
    })

    u.fixtures.zipWithIndex.foreach(p => p._1.foreach(Match.saveOrUpdate(_, p._2, newIndex)))

    Some(newIndex)
  }

  //todo: zwracanie None w przypadku fackupu
  private def update(t: RoundUnit, parentRoundId: Long)(implicit rs: DBSessionRequest[AnyContent]): Option[Long] = { ???
  }
}
