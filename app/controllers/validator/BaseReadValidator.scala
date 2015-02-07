package controllers.validator

import scala.slick.jdbc.JdbcBackend

trait BaseReadValidator[A] {
  def validateGetRequest(serverSideEntity: A)(implicit rs: JdbcBackend#Session): Unit
  def validateGetAllRequest(implicit rs: JdbcBackend#Session): Unit
}
