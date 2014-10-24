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

INSERT INTO territories VALUES (1, 'Podkarpackie', 2101732, 3, 'PLPK');
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

INSERT INTO territories VALUES (2, 'Lubelskie', 2175251, 3, 'PLLU');
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lublin', 343598, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Chełm', 65481, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zamość', 65255, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Biała Podlaska', 57658, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Puławy', 49100, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Świdnik', 40225, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kraśnik', 35788, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łuków', 30757, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Biłgoraj', 27169, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lubartów', 22543, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łęczna', 19926, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tomaszów Lubelski', 19983, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Krasnystaw', 19539, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Hrubieszów', 18585, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Dęblin', 17057, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Międzyrzec Podlaski', 17117, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Radzyń Podlaski', 16188, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Włodawa', 13643, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Janów Lubelski', 12092, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Parczew', 10953, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ryki', 9955, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Poniatowa', 9646, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Opole Lubelskie', 8860, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Bełżyce', 6765, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Terespol', 5815, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Bychawa', 5178, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Szczebrzeszyn', 5245, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rejowiec Fabryczny', 4515, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nałęczów', 4033, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tarnogród', 3464, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kock', 3412, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zwierzyniec', 3347, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Krasnobród', 3117, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Stoczek Łukowski', 2700, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Piaski', 2672, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kazimierz Dolny', 2607, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Annopol', 2664, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Józefów', 2520, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tyszowce', 2171, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łaszczów', 2187, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ostrów Lubelski', 2207, 2, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Frampol', 1482, 2, 0, 0);

INSERT INTO territories VALUES (6, 'Lithuania', 2944459, 4, 'LT');
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Vilnius', 529022, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kaunas', 304012, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Klaipėda', 157305, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Šiauliai', 105610, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Panevėžys', 96328, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Alytus', 56357, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Marijampolė', 38846, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mažeikiai', 35997, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jonava', 29353, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Utena', 27484, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kėdainiai', 25654, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Telšiai', 24295, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tauragė', 23516, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ukmergė', 22510, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Visaginas', 20532, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Plungė', 19153, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kretinga', 18816, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Šilutė', 17002, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Radviliškis', 16527, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Palanga', 15358, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Gargždai', 14937, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Druskininkai', 13762, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rokiškis', 13637, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Biržai', 11666, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Elektrėnai', 11592, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kuršėnai', 11409, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Vilkaviškis', 11038, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Raseiniai', 10890, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jurbarkas', 10878, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Garliava', 10671, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lentvaris', 10610, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Grigiškės', 10394, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Anykščiai', 9968, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Prienai', 9394, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Joniškis', 9309, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Varėna', 8974, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kelmė', 8828, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Naujoji Akmenė', 8532, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kaišiadorys', 8243, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pasvalys', 7138, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kupiškis', 6931, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zarasai', 6779, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Šalčininkai', 6573, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kazlų Rūda', 6342, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Molėtai', 6204, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Širvintos', 6075, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Skuodas', 6032, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Šakiai', 5773, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pabradė', 5723, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ignalina', 5702, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Šilalė', 5454, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Švenčionėliai', 5328, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kybartai', 5223, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nemenčinė', 4933, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pakruojis', 4919, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Švenčionys', 4845, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Trakai', 4837, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Vievis', 4662, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lazdijai', 4357, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kalvarija', 4349, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rietavas', 3574, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Žiežmariai', 3453, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Eišiškės', 3276, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ariogala', 2939, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Neringa', 2752, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Šeduva', 2662, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Akmenė', 2504, 6, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Birštonas', 2475, 6, 0, 0);

INSERT INTO territories VALUES (7, 'Podlaskie', 1197610, 3, 'PLPD');
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Białystok', 295282, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Suwałki', 69317, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łomża', 62711, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Augustów', 30610, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Bielsk Podlaski', 26336, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zambrów', 22451, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Grajewo', 22246, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Hajnówka', 21559, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sokółka', 18765, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łapy', 16005, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Siemiatycze', 14766, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kolno', 10579, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Wasilków', 10511, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mońki', 10352, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Czarna Białostocka', 9671, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Wysokie Mazowieckie', 9503, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Dąbrowa Białostocka', 5874, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Choroszcz', 5782, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sejny', 5650, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ciechanowiec', 4840, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Supraśl', 4679, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Brańsk', 3867, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Szczuczyn', 3489, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Michałowo', 3177, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Knyszyn', 2850, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Czyżew', 2636, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Krynki', 2511, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zabłudów', 2495, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lipsk', 2443, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Stawiski', 2379, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Szepietowo', 2282, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Suchowola', 2274, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nowogród', 2197, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Drohiczyn', 2125, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tykocin', 2014, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Goniądz', 1900, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jedwabne', 1717, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rajgród', 1626, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kleszczele', 1345, 7, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Suraż', 1008 , 7, 0, 0);

INSERT INTO territories VALUES (8, 'Świętokrzyskie', 1281796, 3, 'PLSW');
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kielce', 199870, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ostrowiec Świętokrzyski', 72277, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Starachowice', 51158, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Skarżysko-Kamienna', 47538, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sandomierz', 24552, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Końskie', 20281, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Busko-Zdrój', 16694, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jędrzejów', 15851, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Staszów', 15384, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pińczów', 11306, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Włoszczowa', 10545, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Suchedniów', 8714, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Połaniec', 8347, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Opatów', 6767, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sędziszów', 6707, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Stąporków', 5996, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kazimierza Wielka', 5748, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ożarów', 4730, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Chęciny', 4438, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Małogoszcz', 3929, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Chmielnik', 3921, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ćmielów', 3157, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kunów', 3098, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Daleszyce', 2911, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Wąchock', 2865, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Koprzywnica', 2575, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Bodzentyn', 2274, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Osiek', 2004, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zawichost', 1861, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Skalbmierz', 1315, 8, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Działoszyce', 981 , 8, 0, 0);

INSERT INTO territories VALUES (9, 'Mazowieckie', 5164612, 3, 'PLMZ');
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Warszawa', 1724404, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Radom', 218466, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Płock', 122815, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Siedlce', 76347, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pruszków', 59570, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Legionowo', 54231, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ostrołęka', 52917, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Otwock', 45044, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Piaseczno', 44869, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ciechanów', 44797, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Żyrardów', 41096, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mińsk Mazowiecki', 39880, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Wołomin', 37505, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sochaczew', 37480, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ząbki', 31884, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mława', 30880, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Grodzisk Mazowiecki', 29907, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Marki', 29032, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nowy Dwór Mazowiecki', 28287, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Wyszków', 27222, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Piastów', 22826, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ostrów Mazowiecka', 22796, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Płońsk', 22494, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kobyłka', 20855, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Józefów', 19914, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pionki', 19382, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sulejówek', 19311, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pułtusk', 19228, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Gostynin', 19092, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sokołów Podlaski', 18720, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sierpc', 18491, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kozienice', 18277, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zielonka', 17398, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Konstancin-Jeziorna', 17391, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Przasnysz', 17326, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Garwolin', 17146, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łomianki', 16639, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Grójec', 16453, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Milanówek', 16410, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Brwinów', 13090, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Węgrów', 12796, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Błonie', 12570, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Szydłowiec', 12064, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Warka', 11727, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Góra Kalwaria', 11685, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Radzymin', 11378, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ożarów Mazowiecki', 10561, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Karczew', 10160, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Maków Mazowiecki', 10118, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Żuromin', 9022, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zwoleń', 8112, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tłuszcz', 7989, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nasielsk', 7650, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Białobrzegi', 7132, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łosice', 7099, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łochów', 6786, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mszczonów', 6460, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Przysucha', 6146, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lipsko', 5816, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Iłża', 5054, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łaskarzew', 4914, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Raciąż', 4662, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pilawa', 4438, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Skaryszew', 4308, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Gąbin', 4142, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Serock', 4130, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tarczyn', 4127, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Żelechów', 4081, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nowe Miasto nad Pilicą', 3920, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Podkowa Leśna', 3869, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Halinów', 3654, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zakroczym', 3241, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Myszyniec', 3170, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Glinojeck', 3150, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Drobin', 2992, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Chorzele', 2939, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kałuszyn', 2934, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Różan', 2730, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Wyszogród', 2709, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mogielnica', 2386, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kosów Lacki', 2187, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Brok', 1977, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Bieżuń', 1911, 9, 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mordy', 1819, 9, 0, 0);

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
