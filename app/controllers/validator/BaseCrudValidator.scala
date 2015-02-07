package controllers.validator

import scala.slick.jdbc.JdbcBackend

trait BaseCrudValidator[A] extends BaseReadValidator[A] {
  def validateEditRequest(serverSideEntity: A, frontSideEntity: A)(implicit rs: JdbcBackend#Session): Unit
  def validateCreateRequest(frontSideEntity: A)(implicit rs: JdbcBackend#Session): Unit
  def validateDeleteRequest(serverSideEntity: A)(implicit rs: JdbcBackend#Session): Unit
}
