package db.dao

import models.BaseEntity

import scala.slick.jdbc.JdbcBackend
import models.Common._
import play.api.db.slick.Config.driver.simple._

abstract class BaseReadDao[A <: BaseEntity] extends BaseDao[A]{
  def find(ids: Seq[Id])(implicit rs: JdbcBackend#Session): Option[A] =
    fromFilter(_.id === ids.last).headOption

  def findAll(ids: Seq[Id])(implicit rs: JdbcBackend#Session): List[A] =
    ds.list.map(_.toEntity)

  protected def fromFilter(filter: TableType => Column[Boolean])(implicit rs: JdbcBackend#Session): List[A] =
    ds.filter(filter).list.map(_.toEntity)
}
