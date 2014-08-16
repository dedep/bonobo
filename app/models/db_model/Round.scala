package models.db_model

import com.typesafe.scalalogging.slf4j.Logger
import models.core.Common.Pot
import models.core.round.RoundUnit
import models.core.team.Team
import models.table.RoundsTable
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._

import play.api.db.slick._
import play.api.mvc.AnyContent

import scala.slick.lifted.TableQuery

object Round {
  val ds = TableQuery[RoundsTable]
  val autoIncInsert = ds.map(e => ()) returning ds.map(_.id)

  private val log = Logger(LoggerFactory.getLogger(this.getClass))

  def fromId(id: Long)(implicit rs: DBSessionRequest[AnyContent]): Option[models.core.round.Round] =
    (for (round <- Round.ds if round.id === id) yield round).firstOption match {
      case None => None
      case Some((id: Long, clazz: String, step: Int, isPreliminary: Boolean, tournamentId: Long)) =>
        try { // todo: wydzielić metodę do testów!!
          val citiesPots = getRoundCitiesAndPots(id)
          val cities = citiesPots.map(_._1)
          val pots = groupCitiesByPots(citiesPots)

          val units = Unit.ds.filter(_.roundId === id).list.map(a => Unit.fromId(a._1).get)

          if (isPreliminary) {
            Some(Class.forName(clazz)
              .getConstructor(classOf[() => List[Team]], classOf[() => List[Pot]], classOf[() => List[RoundUnit]], classOf[Int], classOf[Boolean])
              .newInstance(() => cities, () => pots, () => units, Int.box(step), Boolean.box(true))
              .asInstanceOf[models.core.round.Round])
          } else {
            Some(Class.forName(clazz)
              .getConstructor(classOf[() => List[Team]], classOf[() => List[Pot]], classOf[() => List[RoundUnit]], classOf[Int])
              .newInstance(() => cities, () => pots, () => units, Int.box(step))
              .asInstanceOf[models.core.round.Round])
          }
        } catch {
          case cnfe: ClassNotFoundException => log.error("Cannot find saved in database class", cnfe) ; None
          case cce: ClassCastException => log.error("Cannot cast saved in database class into Round", cce) ; None
        }
    }

  //todo: może to przenieść do objectu RoundsCities?
  private def getRoundCitiesAndPots(roundId: Long)(implicit rs: DBSessionRequest[AnyContent]): List[(City, Int)] =
    (for (roundCity <- RoundsCities.ds if roundCity.roundId === roundId) yield roundCity)
      .list.map(rc => (City.fromId(rc._3).get, rc._4))

  //todo: test
  private def groupCitiesByPots(citiesPots: List[(City, Int)]): List[Pot] =
    citiesPots.groupBy(_._2).toList.map(_._2.map(_._1))
}