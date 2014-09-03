package models.db_model

import models.core.Common.Fixture
import models.core.round.RoundUnit
import models.core.round.result.TeamResult
import models.core.team.Team
import models.table.{UnitsCitiesTable, UnitsTable}
import play.api.db.slick.Config.driver.simple._

import play.api.db.slick._
import play.api.mvc.AnyContent

import scala.slick.jdbc.JdbcBackend

object Unit {
  val ds = TableQuery[UnitsTable]
  val citiesDs = TableQuery[UnitsCitiesTable]

  def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[RoundUnit] =
    (for (m <- ds if m.id === id) yield m).firstOption match {
      case None => None
      case Some((roundId: Long, clazz: String)) => { //todo: refaktor

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
    matchFixtureList.groupBy(_._2).toList.sortBy(_._1).map(_._2.map(_._1))

  def saveOrUpdate(u: RoundUnit, parentRoundId: Long)(implicit rs: JdbcBackend#Session): Long = {
    if (Round.fromId(parentRoundId).isEmpty)
      throw new IllegalStateException("Unit cannot refer to non-existent round")

    val parentRoundTeams = Round.fromId(parentRoundId).get.teams
    if (!u.teams.forall(team => parentRoundTeams.exists(t => t.id == team.id)))
      throw new IllegalStateException("Unit cannot contain cities that are not from parent round")

    u.teams.foreach(team => City.saveOrUpdate(team.asInstanceOf[City]))

    if (u.id.nonEmpty && Unit.fromId(u.id.get).nonEmpty) update(u, parentRoundId)
    else save(u, parentRoundId)
  }

  private def save(u: RoundUnit, parentRoundId: Long)(implicit rs: JdbcBackend#Session): Long = {
    val newIndex = (ds returning ds.map(_.id)) += (parentRoundId, u.getClass.getName)

    citiesDs ++= u.teams.map(team => {
      val teamResult = u.results.find(_.team == team).getOrElse(throw new IllegalStateException("Cannot find CityResult"))
      (team.id, newIndex, teamResult.points, teamResult.goalsScored, teamResult.goalsConceded)
    })

    u.fixtures.zipWithIndex.foreach(p => p._1.foreach(Match.saveOrUpdate(_, p._2, newIndex)))

    newIndex
  }

  private def update(u: RoundUnit, parentRoundId: Long)(implicit rs: JdbcBackend#Session): Long = {
    println("Updating following RoundUnit: id = " + u.id.get + ", results = " + u.results.mkString(", "))
    ds.filter(_.id === u.id.get).update((parentRoundId, u.getClass.getName))

    u.teams.foreach(team => {
      val teamResult = u.results.find(_.team.id == team.id).getOrElse(throw new IllegalStateException())

      citiesDs.filter(c => c.cityId === team.id && c.unitId === u.id.get).update(team.id, u.id.get,
        teamResult.points,
        teamResult.goalsScored,
        teamResult.goalsConceded)
    })

    if (u.fixtures.nonEmpty) u.fixtures.foreach(fix => fix.foreach(m => Match.saveOrUpdate(m, u.fixtures.indexOf(fix), u.id.get)))

    u.id.get
  }
}
