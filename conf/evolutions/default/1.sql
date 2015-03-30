# --- !Ups

CREATE TABLE territories
(
  id bigserial PRIMARY KEY,
  code varchar(9) UNIQUE,
  name text NOT NULL,
  population bigint NOT NULL CHECK (population > 0),
  container bigint NULL references territories ON UPDATE CASCADE ON DELETE CASCADE,
  is_country boolean NOT NULL,
  modifiable boolean NOT NULL DEFAULT TRUE
);

CREATE TABLE cities
(
  id bigserial PRIMARY KEY,
  name text NOT NULL,
  population bigint NOT NULL CHECK (population > 0),
  points int NOT NULL DEFAULT 0,
  container bigint NOT NULL references territories ON UPDATE CASCADE ON DELETE CASCADE,
  latitude double precision NULL,
  longitude double precision NULL
);

CREATE TABLE tournaments
(
  id bigserial PRIMARY KEY,
  name text NOT NULL,
  status text NOT NULL,
  territory bigint NOT NULL REFERENCES territories
);

CREATE TABLE tournament_rules
(
  tournament_id bigserial PRIMARY KEY references tournaments,
  win_points double precision NOT NULL,
  draw_points double precision NOT NULL,
  lose_points double precision NOT NULL
);

CREATE TABLE cities_tournaments
(
  city_id bigint references cities NOT NULL,
  tournament_id bigint references tournaments NOT NULL,
  is_playing boolean NOT NULL,
  PRIMARY KEY(tournament_id, city_id)
);

CREATE TABLE rounds
(
  id bigserial PRIMARY KEY,
  name text NOT NULL,
  class text NOT NULL,
  step integer NOT NULL,
  is_preliminary boolean NOT NULL,
  tournament_id bigint references tournaments NOT NULL,
  status text NOT NULL
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
  class text NOT NULL,
  unit_name text NOT NULL
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
  points double precision NOT NULL CHECK(points >= 0),
  goals_scored integer NOT NULL CHECK(goals_scored >= 0),
  goals_conceded integer NOT NULL CHECK(goals_conceded >= 0),
  wins integer NOT NULL CHECK(wins >= 0),
  draws integer NOT NULL CHECK(draws >= 0),
  loses integer NOT NULL CHECK(loses >= 0),
  promoted boolean NULL,
  PRIMARY KEY(city_id, unit_id)
);

CREATE INDEX territories_container_idx ON territories(container);
CREATE INDEX territories_code_idx ON territories(code);
CREATE INDEX cities_container_idx ON cities(container);
CREATE INDEX cities_points_idx ON cities(points);
CREATE INDEX cities_tournaments_city_idx ON cities_tournaments(city_id);
CREATE INDEX cities_tournaments_tournament_idx ON cities_tournaments(tournament_id);
CREATE INDEX rounds_tournament_idx ON rounds(tournament_id);
CREATE INDEX rounds_cities_round_idx ON rounds_cities(round_id);
CREATE INDEX rounds_cities_city_idx ON rounds_cities(city_id);
CREATE INDEX rounds_cities_pot_idx ON rounds_cities(pot);
CREATE INDEX units_round_idx ON units(round_id);
CREATE INDEX matches_a_team_idx ON matches(a_team_id);
CREATE INDEX matches_b_team_idx ON matches(b_team_id);
CREATE INDEX matches_unit_idx ON matches(unit_id);
CREATE INDEX matches_fixture_idx ON matches(fixture);
CREATE INDEX units_cities_city_idx ON units_cities(city_id);
CREATE INDEX units_cities_unit_idx ON units_cities(unit_id);
CREATE INDEX tournaments_territory_idx ON tournaments(territory);

# --- !Downs
DROP TABLE IF EXISTS cities_tournaments;
DROP TABLE IF EXISTS units_cities;
DROP TABLE IF EXISTS rounds_cities;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS cities;
DROP TABLE IF EXISTS units;
DROP TABLE IF EXISTS rounds;
DROP TABLE IF EXISTS tournament_rules;
DROP TABLE IF EXISTS tournaments;
DROP TABLE IF EXISTS territories;
