package db.dao

import com.typesafe.scalalogging.slf4j.Logger
import models.BaseEntity
import org.slf4j.LoggerFactory

import scala.slick.jdbc.JdbcBackend
import play.api.db.slick.Config.driver.simple._
import models.Common._
import utils.FunLogger._

trait BaseCrudDao[A <: BaseEntity] extends BaseReadDao[A] {
  private implicit lazy val log = Logger(LoggerFactory.getLogger("app"))

  def delete(e: A)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.id === e.id)
      .delete
      .plainLog(s"Deleting entity ${e.getClass} with id = ${e.id}.").info()

  def insert(a: A)(implicit rs: JdbcBackend#Session): Id =
    (ds returning ds.map(_.id)) += dbRowService.fromEntity(a).asInstanceOf[RowType]
      .log(id => s"Adding entity ${a.getClass} with id = $id").info()

  def insertAll(a: Seq[A])(implicit rs: JdbcBackend#Session): Seq[Id] =
    (ds returning ds.map(_.id)) ++= a.map { e =>
      dbRowService.fromEntity(e).asInstanceOf[RowType]
      .log(id => s"Adding entity ${a.getClass} with id = $id").info()
    }

  def update(t: A, oldId: Id)(implicit rs: JdbcBackend#Session): Unit =
    ds.filter(_.id === oldId)
      .update(dbRowService.fromEntity(t).asInstanceOf[RowType])(rs)
      .plainLog(s"Updating entity ${t.getClass} with id = $oldId").info()
}
