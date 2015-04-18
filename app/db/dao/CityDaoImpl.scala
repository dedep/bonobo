package db.dao

import com.typesafe.scalalogging.slf4j.Logger
import db.row.mapper.CityRowMapper
import db.row.model.{TerritoryRow, CityRow}
import db.table._
import models.Common._
import models.territory.City
import org.slf4j.LoggerFactory
import play.api.db.slick.Config.driver.simple._
import scaldi.{Injectable, Injector}

import scala.slick.jdbc.JdbcBackend
import scala.slick.lifted.TableQuery

class CityDaoImpl(implicit inj: Injector) extends CityDao with Injectable {
  private implicit val log = Logger(LoggerFactory.getLogger("app"))

  override protected val ds = TableQuery[CitiesTable]
  override protected val dbRowService = inject[CityRowMapper]

  private val territoryDao = inject[TerritoryDao]

  override val selectQuery =
    for {
      city <- ds
      terr <- territoryDao.selectQuery
      if terr.id === city.territoryId
    } yield (city, terr)

  override def find(id: Seq[Id])(implicit rs: JdbcBackend#Session): Option[City] = fromId(Seq(id.last)).headOption

  override def fromId(ids: Seq[Id])(implicit rs: JdbcBackend#Session): List[City] =
    selectQuery
      .filter(_._1.id inSet ids)
      .list
      .map(fromRow)

  override def fromRow(row: (CityRow, TerritoryRow))(implicit rs: JdbcBackend#Session): City = row match {
    case (citiesRow: CityRow, territoriesRow: TerritoryRow) =>
      new City(Some(citiesRow.id), citiesRow.name, citiesRow.population, citiesRow.points, territoriesRow.toEntity,
        citiesRow.latitude, citiesRow.longitude)
  }

  override def getAllWithinTerritoryCascade(territoryId: Long)(implicit rs: JdbcBackend#Session): List[City] =
    territoryDao.getChildrenTerritories(territoryId) match {
      case Nil => getAllWithinTerritory(territoryId)
      case tr => getAllWithinTerritory(territoryId) ::: tr.flatMap(t => getAllWithinTerritoryCascade(t.id.get))
    }

  override def getAllWithinTerritory(territoryId: Long)(implicit rs: JdbcBackend#Session): List[City] =
    ds.filter(_.id === territoryId).map(_.id).list.map(t => find(t :: Nil).get)
}