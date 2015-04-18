package db.row.mapper

import db.row.model.{BaseRow, CityRow}
import models.territory.City
import scaldi.Injector

class CityRowMapper(implicit inj: Injector) extends BaseRowMapper[City] {
   override def fromEntity(c: City): BaseRow[City] =
      CityRow(c.id.getOrElse(-1), c.name, c.population, c.points, c.territory.id.get, c.latitude, c.longitude)
}