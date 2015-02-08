package db.dao

import scala.slick.jdbc.JdbcBackend

object TestUtils {

  def truncateTestTables(implicit session: JdbcBackend#Session) = {
    execute("TRUNCATE tournaments CASCADE;")
    execute("TRUNCATE rounds CASCADE;")
    execute("TRUNCATE rounds_cities CASCADE;")
    execute("TRUNCATE territories CASCADE;")
    execute("TRUNCATE cities CASCADE;")
    execute("TRUNCATE cities_tournaments CASCADE;")
    execute("TRUNCATE units CASCADE;")
    execute("TRUNCATE units_cities CASCADE;")
    execute("TRUNCATE matches CASCADE;")
  }

  def insertTestTournamentIntoDatabase(implicit session: JdbcBackend#Session) = {
    TestUtils.truncateTestTables
    execute("INSERT INTO territories VALUES ('W', 'World', 1112129931, NULL, false, false);")
    execute("INSERT INTO territories VALUES ('EU', 'Europe', 112129931, 'W', false, false);")
    execute("INSERT INTO territories VALUES ('PL', 'Poland', 12129931, 'EU', true, false);")
    execute("INSERT INTO territories VALUES ('PLPK', 'Podkarpackie', 2129951, 'PL', true, true);")
    execute("INSERT INTO territories VALUES ('PLLU', 'Lubelskie', 2129931, 'PL', true, true);")
    execute("INSERT INTO cities VALUES (1, 'Szczecin', 182028, 0, 'PLLU', 0, 0);")
    execute("INSERT INTO cities VALUES (2, 'Koszalin', 64276, 0, 'PLLU', 0, 0);")
    execute("INSERT INTO cities VALUES (3, 'Szczecinek', 182028, 0, 'PLLU', 0, 0);")
    execute("INSERT INTO cities VALUES (4, 'Darłowo', 61276, 0, 'PLLU', 0, 0);")
    execute("INSERT INTO cities VALUES (5, 'Warszawa', 64276, 0, 'PL', 0, 0);")
    execute("INSERT INTO cities VALUES (6, 'Budapeszt', 182028, 0, 'EU', 0, 0);")
    execute("INSERT INTO cities VALUES (7, 'Nowy Jork', 61276, 0, 'W', 0, 0);")
    execute("INSERT INTO cities VALUES (15, 'Rzeszów', 182028, 0, 'PLPK', 0, 0);")
    execute("INSERT INTO cities VALUES (16, 'Przemyśl', 64276, 0, 'PLPK', 0, 0);")
    execute("INSERT INTO cities VALUES (17, 'Stalowa Wola', 64189, 0, 'PLPK', 0, 0);")
    execute("INSERT INTO cities VALUES (18, 'Mielec', 61238, 0, 'PLPK', 0, 0);")
    execute("INSERT INTO cities VALUES (19, 'Tarnobrzeg', 48558, 0, 'PLPK', 0, 0);")
    execute("INSERT INTO tournaments VALUES (1, 'DB Test tournament', 'NOT_STARTED', 'W');")
    execute("INSERT INTO cities_tournaments VALUES (1, 1, true);")
    execute("INSERT INTO cities_tournaments VALUES (2, 1, true);")
    execute("INSERT INTO cities_tournaments VALUES (3, 1, true);")
    execute("INSERT INTO cities_tournaments VALUES (4, 1, true);")
    execute("INSERT INTO rounds VALUES (1, 'round', 'models.round.GroupRound', 1, false, 1, 'DRAW_POTS');")
    execute("INSERT INTO rounds_cities VALUES (1, 1, 1);")
    execute("INSERT INTO rounds_cities VALUES (1, 2, 0);")
    execute("INSERT INTO rounds_cities VALUES (1, 3, 0);")
    execute("INSERT INTO rounds_cities VALUES (1, 4, 1);")
    execute("INSERT INTO units VALUES (1, 1, 'models.unit.Group', 'Group K');")
    execute("INSERT INTO units_cities VALUES (1, 1, 0, 0, 2, 1, 1, 1);")
    execute("INSERT INTO units_cities VALUES (2, 1, 0, 0, 0, 1, 1, 1);")
    execute("INSERT INTO units_cities VALUES (3, 1, 0, 0, 0, 1, 1, 1);")
    execute("INSERT INTO units_cities VALUES (4, 1, 0, 0, 0, 1, 1, 1);")
    execute("INSERT INTO matches VALUES (1, 1, 0, 1, 2, 1, 4);")
    execute("INSERT INTO matches VALUES (2, 1, 0, 3, 5, 4, 9);")
    execute("INSERT INTO matches VALUES (3, 1, 1, 1, 2, 1, 5);")
    execute("INSERT INTO matches VALUES (4, 1, 1, 3, 5, 4, 10);")
  }

  def execute(sql: String)(implicit session: JdbcBackend#Session): Unit = {
    val stmt = session.createStatement()
    try {
      stmt.executeUpdate(sql)
    } finally {
      stmt.close()
    }
  }
}
