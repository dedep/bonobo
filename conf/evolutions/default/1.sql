# --- !Ups

CREATE TABLE territories
(
  id serial PRIMARY KEY,
  name text NOT NULL,
  population bigint NOT NULL CHECK (population > 0),
  container integer references territories NULL
);

CREATE TABLE cities
(
  id serial PRIMARY KEY,
  name text NOT NULL,
  population bigint NOT NULL CHECK (population > 0),
  points int NOT NULL DEFAULT 0,
  container integer references territories NOT NULL
);

CREATE TABLE tournaments
(
  id serial PRIMARY KEY
);

CREATE TABLE cities_tournaments
(
  id serial PRIMARY KEY,
  city_id integer references cities NOT NULL,
  tournament_id integer references tournaments NOT NULL
);

INSERT INTO territories VALUES (3, 'Poland', 1, NULL);

INSERT INTO territories VALUES (1, 'Podkarpackie', 2129951, 3);
INSERT INTO cities(name, population, container) VALUES ('Rzeszów', 182028, 1);
INSERT INTO cities(name, population, container) VALUES ('Przemyśl', 64276, 1);
INSERT INTO cities(name, population, container) VALUES ('Stalowa Wola', 64189, 1);
INSERT INTO cities(name, population, container) VALUES ('Mielec', 61238, 1);
INSERT INTO cities(name, population, container) VALUES ('Tarnobrzeg', 48558, 1);
INSERT INTO cities(name, population, container) VALUES ('Krosno', 47307, 1);
INSERT INTO cities(name, population, container) VALUES ('Dębica', 47180, 1);
INSERT INTO cities(name, population, container) VALUES ('Jarosław', 39426, 1);
INSERT INTO cities(name, population, container) VALUES ('Sanok', 39375, 1);
INSERT INTO cities(name, population, container) VALUES ('Jasło', 36641, 1);
INSERT INTO cities(name, population, container) VALUES ('Łańcut', 18143, 1);
INSERT INTO cities(name, population, container) VALUES ('Przeworsk', 15915, 1);
INSERT INTO cities(name, population, container) VALUES ('Ropczyce', 15655, 1);
INSERT INTO cities(name, population, container) VALUES ('Nisko', 15479, 1);
INSERT INTO cities(name, population, container) VALUES ('Leżajsk', 14448, 1);
INSERT INTO cities(name, population, container) VALUES ('Lubaczów', 12552, 1);
INSERT INTO cities(name, population, container) VALUES ('Nowa Dęba', 11507, 1);
INSERT INTO cities(name, population, container) VALUES ('Ustrzyki Dolne', 9521, 1);
INSERT INTO cities(name, population, container) VALUES ('Kolbuszowa', 9341, 1);
INSERT INTO cities(name, population, container) VALUES ('Strzyżów', 8930, 1);
INSERT INTO cities(name, population, container) VALUES ('Brzozów', 7631, 1);
INSERT INTO cities(name, population, container) VALUES ('Sędziszów Małopolski', 7481, 1);
INSERT INTO cities(name, population, container) VALUES ('Rudnik nad Sanem', 6883, 1);
INSERT INTO cities(name, population, container) VALUES ('Nowa Sarzyna', 6287, 1);
INSERT INTO cities(name, population, container) VALUES ('Dynów', 6195, 1);
INSERT INTO cities(name, population, container) VALUES ('Głogów Małopolski', 6014, 1);
INSERT INTO cities(name, population, container) VALUES ('Boguchwała', 5796, 1);
INSERT INTO cities(name, population, container) VALUES ('Jedlicze', 5782, 1);
INSERT INTO cities(name, population, container) VALUES ('Lesko', 5674, 1);
INSERT INTO cities(name, population, container) VALUES ('Radymno', 5528, 1);
INSERT INTO cities(name, population, container) VALUES ('Zagórz', 5100, 1);
INSERT INTO cities(name, population, container) VALUES ('Pilzno', 4762, 1);
INSERT INTO cities(name, population, container) VALUES ('Sokołów Małopolski', 4028, 1);
INSERT INTO cities(name, population, container) VALUES ('Rymanów', 3753, 1);
INSERT INTO cities(name, population, container) VALUES ('Pruchnik', 3716, 1);
INSERT INTO cities(name, population, container) VALUES ('Tyczyn', 3602, 1);
INSERT INTO cities(name, population, container) VALUES ('Kańczuga', 3242, 1);
INSERT INTO cities(name, population, container) VALUES ('Oleszyce', 3108, 1);
INSERT INTO cities(name, population, container) VALUES ('Radomyśl Wielki', 3079, 1);
INSERT INTO cities(name, population, container) VALUES ('Brzostek', 2697, 1);
INSERT INTO cities(name, population, container) VALUES ('Sieniawa', 2198, 1);
INSERT INTO cities(name, population, container) VALUES ('Dukla', 2194, 1);
INSERT INTO cities(name, population, container) VALUES ('Błażowa', 2182, 1);
INSERT INTO cities(name, population, container) VALUES ('Narol', 2100, 1);
INSERT INTO cities(name, population, container) VALUES ('Cieszanów', 1962, 1);
INSERT INTO cities(name, population, container) VALUES ('Iwonicz-Zdrój', 1904, 1);
INSERT INTO cities(name, population, container) VALUES ('Przecław', 1682, 1);
INSERT INTO cities(name, population, container) VALUES ('Baranów Sandomierski', 1508, 1);
INSERT INTO cities(name, population, container) VALUES ('Ulanów', 1482, 1);
INSERT INTO cities(name, population, container) VALUES ('Kołaczyce', 1447, 1);

INSERT INTO territories VALUES (2, 'Lubelskie', 2129951, 3);
INSERT INTO cities(name, population, container) VALUES ('Dublin', 1447, 2);

INSERT INTO tournaments VALUES (1);
INSERT INTO cities_tournaments VALUES(1, 11, 1);
INSERT INTO cities_tournaments VALUES(2, 12, 1);

# --- !Downs

DROP TABLE IF EXISTS cities_tournaments;
DROP TABLE IF EXISTS cities;
DROP TABLE IF EXISTS territories;
DROP TABLE IF EXISTS tournaments;