package db.dao

import scala.slick.jdbc.JdbcBackend

trait BaseCrudDao[A, B] extends BaseReadDao[A, B] {
  def delete(t: A)(implicit rs: JdbcBackend#Session): Unit
  def save(t: A)(implicit rs: JdbcBackend#Session): B
  def update(t: A, oldCode: B)(implicit rs: JdbcBackend#Session): Unit
}
