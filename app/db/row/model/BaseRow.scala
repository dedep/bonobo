package db.row.model

import scala.slick.jdbc.JdbcBackend

abstract class BaseRow[A](val id: Long) {
  def toEntity(implicit rs: JdbcBackend#Session): A
}
