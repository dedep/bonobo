package db.dao

import scala.slick.jdbc.JdbcBackend

trait BaseReadDao[A, B] {
  def find(key: B)(implicit rs: JdbcBackend#Session): Option[A]
  def findAll(implicit rs: JdbcBackend#Session): List[A]
}
