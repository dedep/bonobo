package models.db_model

import models.table.CitiesTournamentsTable
import play.api.db.slick.Config.driver.simple._

object CitiesTournaments {
  val ds = TableQuery[CitiesTournamentsTable]
  val autoIncInsert = ds.map(e => (e.cityId, e.tournamentId)) returning ds.map(_.id)
}