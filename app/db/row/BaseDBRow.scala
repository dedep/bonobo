package db.row

import scala.slick.jdbc.JdbcBackend

abstract class BaseDBRow[A](val id: Long) {
  def toEntity(implicit rs: JdbcBackend#Session): A
}

abstract class BaseDBRowService[A] {
  def fromEntity(entity: A): BaseDBRow[A]
}
