package db.row.mapper

import db.row.model.TerritoryRow
import models.territory.Territory
import scaldi.Injector

class TerritoryRowMapper(implicit inj: Injector) extends BaseRowMapper[Territory] {
  override def fromEntity(t: Territory): TerritoryRow =
    TerritoryRow(t.id.getOrElse(-1), t.code, t.name, t.population, t.container.map(_.id.get), t.isCountry, t.modifiable)
}