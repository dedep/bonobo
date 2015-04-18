package db.row.mapper

import db.row.model.BaseRow

abstract class BaseRowMapper[A] {
  def fromEntity(entity: A): BaseRow[A]
}
