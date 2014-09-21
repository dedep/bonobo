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
