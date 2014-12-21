package db.dao

import scala.slick.jdbc.JdbcBackend

object TestUtils {

  def truncateTestTables(implicit session: JdbcBackend#Session) = {
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

  def insertTestTournamentIntoDatabase(implicit session: JdbcBackend#Session) = {
    TestUtils.truncateTestTables
    session.createStatement().executeUpdate("INSERT INTO territories VALUES (5, 'World', 1, NULL, '');")
    session.createStatement().executeUpdate("INSERT INTO territories VALUES (4, 'Europe', 1, 5, '');")
    session.createStatement().executeUpdate("INSERT INTO territories VALUES (3, 'Poland', 1, 4, '');")
    session.createStatement().executeUpdate("INSERT INTO territories VALUES (2, 'Podkarpackie', 2129951, 3, '');")
    session.createStatement().executeUpdate("INSERT INTO territories VALUES (1, 'Zachodniopomorskie', 2129951, 3, '');")
    session.createStatement().executeUpdate("INSERT INTO cities VALUES (1, 'Szczecin', 182028, 0, 1, 0, 0);")
    session.createStatement().executeUpdate("INSERT INTO cities VALUES (2, 'Koszalin', 64276, 0, 1, 0, 0);")
    session.createStatement().executeUpdate("INSERT INTO cities VALUES (3, 'Szczecinek', 182028, 0, 1, 0, 0);")
    session.createStatement().executeUpdate("INSERT INTO cities VALUES (4, 'Darłowo', 61276, 0, 1, 0, 0);")
    session.createStatement().executeUpdate("INSERT INTO cities VALUES (5, 'Warszawa', 64276, 0, 3, 0, 0);")
    session.createStatement().executeUpdate("INSERT INTO cities VALUES (6, 'Budapeszt', 182028, 0, 4, 0, 0);")
    session.createStatement().executeUpdate("INSERT INTO cities VALUES (7, 'Nowy Jork', 61276, 0, 5, 0, 0);")
    session.createStatement().executeUpdate("INSERT INTO cities VALUES (15, 'Rzeszów', 182028, 0, 2, 0, 0);")
    session.createStatement().executeUpdate("INSERT INTO cities VALUES (16, 'Przemyśl', 64276, 0, 2, 0, 0);")
    session.createStatement().executeUpdate("INSERT INTO cities VALUES (17, 'Stalowa Wola', 64189, 0, 2, 0, 0);")
    session.createStatement().executeUpdate("INSERT INTO cities VALUES (18, 'Mielec', 61238, 0, 2, 0, 0);")
    session.createStatement().executeUpdate("INSERT INTO cities VALUES (19, 'Tarnobrzeg', 48558, 0, 2, 0, 0);")
    session.createStatement().executeUpdate("INSERT INTO tournaments VALUES (1, 'DB Test tournament');")
    session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (1, 1);")
    session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (2, 1);")
    session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (3, 1);")
    session.createStatement().executeUpdate("INSERT INTO cities_tournaments VALUES (4, 1);")
    session.createStatement().executeUpdate("INSERT INTO rounds VALUES (1, 'round', 'models.round.GroupRound', 1, false, 1);")
    session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 1, 1);")
    session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 2, 0);")
    session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 3, 0);")
    session.createStatement().executeUpdate("INSERT INTO rounds_cities VALUES (1, 4, 1);")
    session.createStatement().executeUpdate("INSERT INTO units VALUES (1, 1, 'models.unit.Group', 'Group K');")
    session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (1, 1, 0, 0, 2, 1, 1, 1);")
    session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (2, 1, 0, 0, 0, 1, 1, 1);")
    session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (3, 1, 0, 0, 0, 1, 1, 1);")
    session.createStatement().executeUpdate("INSERT INTO units_cities VALUES (4, 1, 0, 0, 0, 1, 1, 1);")
    session.createStatement().executeUpdate("INSERT INTO matches VALUES (1, 1, 0, 1, 2, 1, 4);")
    session.createStatement().executeUpdate("INSERT INTO matches VALUES (2, 1, 0, 3, 5, 4, 9);")
    session.createStatement().executeUpdate("INSERT INTO matches VALUES (3, 1, 1, 1, 2, 1, 5);")
    session.createStatement().executeUpdate("INSERT INTO matches VALUES (4, 1, 1, 3, 5, 4, 10);")
  }
}
