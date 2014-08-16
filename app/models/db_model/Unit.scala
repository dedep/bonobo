package models.db_model

import models.core.Common.Fixture
import models.core.round.RoundUnit
import models.core.round.result.TeamResult
import models.core.team.Team
import models.table.UnitsTable
import play.api.db.slick.Config.driver.simple._

import play.api.db.slick._
import play.api.mvc.AnyContent
import models.core._match.Match

object Unit {
  val ds = TableQuery[UnitsTable]
  val autoIncInsert = ds.map(e => ()) returning ds.map(_.id)

  def fromId(id: Long)(implicit rs: DBSessionRequest[AnyContent]): Option[RoundUnit] =
    (for (m <- ds if m.id === id) yield m).firstOption match {
      case None => None
      case Some((id: Long, roundId: Long, clazz: String)) => {
        val fixtures =
          groupMatchesByFixture(Match.ds.filter(_.unitId === id).list.map(a => (Match.fromId(a._1).get, a._3)))

        val citiesAndResults = UnitsCities.ds.filter(_.unitId === id).list.map(a => {
          val city = City.fromId(a._1).get
          val result = TeamResult(city, a._4, a._5, a._6)

          (city, result)
        })

        val cities = citiesAndResults.map(_._1)
        val results = citiesAndResults.map(_._2)

        Some(Class.forName(clazz)
          .getConstructor(classOf[() => List[Team]], classOf[() => List[Fixture]], classOf[() => List[TeamResult]])
          .newInstance(() => cities, () => fixtures, () => results)
          .asInstanceOf[RoundUnit])
      }
    }

  private def groupMatchesByFixture(matchFixtureList: List[(Match, Int)]): List[Fixture] =
    matchFixtureList.groupBy(_._2).toList.map(_._2.map(_._1))
}
