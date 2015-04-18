package db.table

import db.row.model.BaseRow
import play.api.db.slick.Config.driver.simple._

abstract class BaseTable[A, B <: BaseRow[A]](tag: Tag, tableName: String) extends Table[B](tag, tableName) {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
}