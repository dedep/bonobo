package db.table

import db.row.BaseDBRow
import play.api.db.slick.Config.driver.simple._

abstract class BaseTable[A, B <: BaseDBRow[A]](tag: Tag, tableName: String) extends Table[B](tag, tableName) {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
}