package models.db_model

import models.table.UnitsCitiesTable
import play.api.db.slick.Config.driver.simple._

object UnitsCities {
  val ds = TableQuery[UnitsCitiesTable]
  val autoIncInsert = ds.map(e => (e.cityId, e.unitId, e.points, e.goalsScored, e.goalsConceded)) returning ds.map(_.id)
}
