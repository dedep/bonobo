# --- !Ups

CREATE TABLE territories
(
  id bigserial PRIMARY KEY,
  name text NOT NULL,
  population bigint NOT NULL CHECK (population > 0),
  container bigint references territories NULL,
  code varchar(20)
);

CREATE TABLE cities
(
  id bigserial PRIMARY KEY,
  name text NOT NULL,
  population bigint NOT NULL CHECK (population > 0),
  points int NOT NULL DEFAULT 0,
  container bigint references territories NOT NULL,
  latitude double precision NULL,
  longitude double precision NULL
);

CREATE TABLE tournaments
(
  id bigserial PRIMARY KEY,
  name varchar(255) NOT NULL
);

CREATE TABLE cities_tournaments
(
  city_id bigint references cities NOT NULL,
  tournament_id bigint references tournaments NOT NULL,
  PRIMARY KEY(tournament_id, city_id)
);

CREATE TABLE rounds
(
  id bigserial PRIMARY KEY,
  name varchar(255) NOT NULL,
  class varchar(255) NOT NULL,
  step integer NOT NULL,
  is_preliminary boolean NOT NULL,
  tournament_id bigint references tournaments NOT NULL,
  round_order bigint NOT NULL DEFAULT 0,
  unique (tournament_id, round_order)
);

CREATE TABLE rounds_cities
(
  round_id bigint references rounds NOT NULL,
  city_id bigint references cities NOT NULL,
  pot integer NULL,
  PRIMARY KEY(round_id, city_id)
);

CREATE TABLE units
(
  id bigserial PRIMARY KEY,
  round_id bigint references rounds,
  class varchar(255) NOT NULL,
  unit_name varchar(255) NOT NULL
);

CREATE TABLE matches
(
  id bigserial PRIMARY KEY,
  unit_id bigint references units NOT NULL,
  fixture integer NOT NULL,
  a_team_id bigint references cities NOT NULL,
  a_team_goals integer NULL,
  b_team_id bigint references cities NOT NULL,
  b_team_goals integer NULL,
  play_date timestamp NULL
);

CREATE TABLE units_cities
(
  city_id bigint references cities NOT NULL,
  unit_id bigint references units NOT NULL,
  points integer NOT NULL CHECK(points >= 0),
  goals_scored integer NOT NULL CHECK(goals_scored >= 0),
  goals_conceded integer NOT NULL CHECK(goals_conceded >= 0),
  wins integer NOT NULL CHECK(wins >= 0),
  draws integer NOT NULL CHECK(draws >= 0),
  loses integer NOT NULL CHECK(loses >= 0),
  PRIMARY KEY(city_id, unit_id)
);

CREATE OR REPLACE FUNCTION rounds_id_auto()
    RETURNS trigger AS $$
DECLARE
    _rel_id constant int := 'rounds'::regclass::int;
BEGIN

    -- Obtain an advisory lock on this table/group.
    PERFORM pg_advisory_lock(_rel_id);

    SELECT  COALESCE(MAX(round_order) + 1, 1)
    INTO    NEW.round_order
    FROM    rounds
    WHERE   tournament_id = NEW.tournament_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql STRICT;

CREATE TRIGGER rounds_id_auto
    BEFORE INSERT ON rounds
    FOR EACH ROW WHEN (NEW.round_order = 0)
    EXECUTE PROCEDURE rounds_id_auto();

CREATE OR REPLACE FUNCTION rounds_id_auto_unlock()
    RETURNS trigger AS $$
DECLARE
    _rel_id constant int := 'rounds'::regclass::int;
BEGIN
    -- Release the lock.
    PERFORM pg_advisory_unlock(_rel_id);

    RETURN NEW;
END;
$$ LANGUAGE plpgsql STRICT;

CREATE TRIGGER rounds_id_auto_unlock
    AFTER INSERT ON rounds
    FOR EACH ROW
    EXECUTE PROCEDURE rounds_id_auto_unlock();

INSERT INTO territories VALUES (4, 'World', 7243000000, NULL, 'W');
INSERT INTO territories VALUES (3, 'Poland', 1, 4, 'PL');
INSERT INTO territories VALUES (5, 'Great Britain', 1, 4, 'GB');

INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, 3, 'PLPK');
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rzeszów', 182028, 1, 50.33, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Przemyśl', 64276, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Stalowa Wola', 64189, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mielec', 61238, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tarnobrzeg', 48558, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Krosno', 47307, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Dębica', 47180, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jarosław', 39426, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sanok', 39375, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jasło', 36641, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łańcut', 18143, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Przeworsk', 15915, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ropczyce', 15655, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nisko', 15479, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Leżajsk', 14448, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lubaczów', 12552, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nowa Dęba', 11507, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ustrzyki Dolne', 9521, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kolbuszowa', 9341, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Strzyżów', 8930, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Brzozów', 7631, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sędziszów Małopolski', 7481, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rudnik nad Sanem', 6883, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nowa Sarzyna', 6287, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Dynów', 6195, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Głogów Małopolski', 6014, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Boguchwała', 5796, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jedlicze', 5782, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lesko', 5674, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Radymno', 5528, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zagórz', 5100, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pilzno', 4762, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sokołów Małopolski', 4028, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rymanów', 3753, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pruchnik', 3716, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tyczyn', 3602, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kańczuga', 3242, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Oleszyce', 3108, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Radomyśl Wielki', 3079, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Brzostek', 2697, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sieniawa', 2198, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Dukla', 2194, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Błażowa', 2182, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Narol', 2100, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Cieszanów', 1962, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Iwonicz-Zdrój', 1904, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Przecław', 1682, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Baranów Sandomierski', 1508, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ulanów', 1482, 1, 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kołaczyce', 1447, 1, 52.02, 22);

INSERT INTO territories VALUES (2, 'Lubelskie', 2129951, 3, 'PLLU');
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Dublin', 12473, 2, 52.02, 22);

# --- !Downs

DROP TABLE IF EXISTS cities_tournaments;
DROP TABLE IF EXISTS units_cities;
DROP TABLE IF EXISTS rounds_cities;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS cities;
DROP TABLE IF EXISTS territories;
DROP TABLE IF EXISTS units;
DROP TABLE IF EXISTS rounds;
DROP TABLE IF EXISTS tournaments;
