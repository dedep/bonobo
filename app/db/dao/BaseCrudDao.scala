package db.dao

import scala.slick.jdbc.JdbcBackend

trait BaseCrudDao[A] extends BaseReadDao[A] {
  def delete(t: A)(implicit rs: JdbcBackend#Session): Unit
  def save(t: A)(implicit rs: JdbcBackend#Session): Long
  def update(t: A, oldCode: Long)(implicit rs: JdbcBackend#Session): Unit
}
