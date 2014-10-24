package db_access.dao.unit

import db_access.dao._match.MatchDao
import db_access.dao.city.CityDao
import db_access.table.{UnitsCitiesTable, UnitsTable}
import models.Common._
import models.round.RoundUnit
import models.round.result.TeamResult
import models.team.Team
import models.territory.City
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend
import scala.util.Sorting

class UnitDaoImpl(implicit inj: Injector) extends UnitDao with Injectable {
  private val ds = TableQuery[UnitsTable]
  private val citiesDs = TableQuery[UnitsCitiesTable]

  private val matchDao = inject[MatchDao]
  private val cityDao = inject[CityDao]

  override def fromId(id: Long)(implicit rs: JdbcBackend#Session): Option[RoundUnit] =
    (for (m <- ds if m.id === id) yield m).firstOption match {
      case None => None
      case Some((roundId: Long, clazz: String, name: String)) => instantiateUnitFromTableRow(name, id, clazz, roundId)
    }

  private def instantiateUnitFromTableRow(name: String, id: Long, clazz: String, roundId: Long)(implicit rs: JdbcBackend#Session): Option[RoundUnit] = {
    val fixtures = matchDao.getFixturesWithinUnit(id)
    val cities = getCities(id)
    val results = getResults(id)

    Some(Class.forName(clazz)
      .getConstructor(classOf[String], classOf[() => List[Team]], classOf[() => List[Fixture]], classOf[() => List[TeamResult]], classOf[Option[Long]])
      .newInstance(name, () => cities, () => fixtures, () => results, Some(id))
      .asInstanceOf[RoundUnit])
  }

  private def getCities(id: Long)(implicit rs: JdbcBackend#Session): List[City] = {
    val citiesAndResults = citiesDs.filter(_.unitId === id).list.map(a => {
      val city = cityDao.fromId(a._1).get
      val result = TeamResult(city, a._3, a._4, a._5)

      (city, result)
    })

    citiesAndResults.map(_._1)
  }

  private def getResults(id: Long)(implicit rs: JdbcBackend#Session): List[TeamResult] = {
    val citiesAndResults = citiesDs.filter(_.unitId === id).list.map(a => {
      val city = cityDao.fromId(a._1).get
      val result = TeamResult(city, a._3, a._4, a._5, a._6, a._7, a._8)

      (city, result)
    })

    Sorting.stableSort(citiesAndResults.map(_._2)).toList.reverse
  }

  override def saveOrUpdate(u: RoundUnit, parentRoundId: Long)(implicit rs: JdbcBackend#Session): Long = {
    if (u.id.nonEmpty && fromId(u.id.get).nonEmpty) update(u, parentRoundId)
    else save(u, parentRoundId)
  }

  override def getAllWithinRound(roundId: Long)(implicit rs: JdbcBackend#Session): List[RoundUnit] =
    ds.filter(_.roundId === roundId).sortBy(_.name).map(_.id).list.map(fromId(_).get)

  private def save(u: RoundUnit, parentRoundId: Long)(implicit rs: JdbcBackend#Session): Long = {
    val newIndex = (ds returning ds.map(_.id)) += (parentRoundId, u.getClass.getName, u.name)

    citiesDs ++= u.teams.map(team => {
      val teamResult = u.results.find(_.team == team).getOrElse(throw new IllegalStateException("Cannot find CityResult"))
      (team.id,
        newIndex,
        teamResult.points,
        teamResult.goalsScored,
        teamResult.goalsConceded,
        teamResult.wins,
        teamResult.draws,
        teamResult.loses)
    })

    u.fixtures.zipWithIndex.foreach(p => p._1.foreach(matchDao.saveOrUpdate(_, p._2, newIndex)))

    newIndex
  }

  private def update(u: RoundUnit, parentRoundId: Long)(implicit rs: JdbcBackend#Session): Long = {
    println("Updating following RoundUnit: id = " + u.id.get + ", results = " + u.results.mkString(", "))
    ds.filter(_.id === u.id.get).update((parentRoundId, u.getClass.getName, u.name))

    u.teams.foreach(team => {
      val teamResult = u.results.find(_.team.id == team.id).getOrElse(throw new IllegalStateException())

      citiesDs.filter(c => c.cityId === team.id && c.unitId === u.id.get).update(team.id, u.id.get,
        teamResult.points,
        teamResult.goalsScored,
        teamResult.goalsConceded,
        teamResult.wins,
        teamResult.draws,
        teamResult.loses)
    })

    if (u.fixtures.nonEmpty) u.fixtures.foreach(fix => fix.foreach(m => matchDao.saveOrUpdate(m, u.fixtures.indexOf(fix), u.id.get)))

    u.id.get
  }
}
