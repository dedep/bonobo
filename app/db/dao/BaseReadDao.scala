package db.dao

import scala.slick.jdbc.JdbcBackend

trait BaseReadDao[A] {
  def find(key: Long)(implicit rs: JdbcBackend#Session): Option[A]
  def findAll(implicit rs: JdbcBackend#Session): List[A]
}
