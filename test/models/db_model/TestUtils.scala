package models.db_model

import scala.slick.jdbc.JdbcBackend

object TestUtils {

  def truncateTestTables(session: JdbcBackend#Session) = {
    session.createStatement().executeUpdate("TRUNCATE tournaments CASCADE;")
    session.createStatement().executeUpdate("TRUNCATE rounds CASCADE;")
    session.createStatement().executeUpdate("TRUNCATE rounds_cities CASCADE;")
    session.createStatement().executeUpdate("TRUNCATE territories CASCADE;")
    session.createStatement().executeUpdate("TRUNCATE cities CASCADE;")
    session.createStatement().executeUpdate("TRUNCATE cities_tournaments CASCADE;")
    session.createStatement().executeUpdate("TRUNCATE units CASCADE;")
    session.createStatement().executeUpdate("TRUNCATE units_cities CASCADE;")
    session.createStatement().executeUpdate("TRUNCATE matches CASCADE;")
  }
}
