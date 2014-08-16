package models.db_model

import models.table.RoundsCitiesTable
import play.api.db.slick.Config.driver.simple._

object RoundsCities {
  val ds = TableQuery[RoundsCitiesTable]
  val autoIncInsert = ds.map(e => (e.roundId, e.cityId, e.pot)) returning ds.map(_.id)
}
