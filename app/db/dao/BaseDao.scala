package db.dao

import db.row.{BaseDBRowService, BaseDBRow}
import db.table.BaseTable
import models.BaseEntity

import scala.slick.lifted.TableQuery

trait BaseDao[A <: BaseEntity] {
  protected val dbRowService: BaseDBRowService[A]
  protected val ds: TableQuery[TableType]

  protected type RowType <: BaseDBRow[A]
  protected type TableType <: BaseTable[A, RowType]
}
