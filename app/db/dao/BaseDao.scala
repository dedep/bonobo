package db.dao

import db.row.mapper.BaseRowMapper
import db.row.model.BaseRow
import db.table.BaseTable
import models.BaseEntity

import scala.slick.lifted.TableQuery

trait BaseDao[A <: BaseEntity] {
  protected val dbRowService: BaseRowMapper[A]
  protected val ds: TableQuery[TableType]

  protected type RowType <: BaseRow[A]
  protected type TableType <: BaseTable[A, RowType]
}
