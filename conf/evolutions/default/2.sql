# --- !Ups

INSERT INTO territories VALUES ('W',  'World', 7243000000, NULL, false, false);
INSERT INTO territories VALUES ('PL', 'Poland', 38483957, 'W', true);
INSERT INTO territories VALUES ('GB', 'United Kingdom', 63181775, 'W', true);
INSERT INTO territories VALUES ('LT', 'Lithuania', 2921920, 'W', true);
INSERT INTO territories VALUES ('NO', 'Norway', 5136700, 'W', true);
INSERT INTO territories VALUES ('IS', 'Iceland', 325671, 'W', true);
INSERT INTO territories VALUES ('SE', 'Sweden', 9716962, 'W', true);
INSERT INTO territories VALUES ('FI', 'Finland', 5474094, 'W', true);
INSERT INTO territories VALUES ('EE', 'Estonia', 1315819, 'W', true);
INSERT INTO territories VALUES ('LV', 'Latvia', 1990300, 'W', true);
INSERT INTO territories VALUES ('DK', 'Denmark', 5655750, 'W', true);
INSERT INTO territories VALUES ('DE', 'Germany', 80716000, 'W', true);
INSERT INTO territories VALUES ('IE', 'Ireland', 6378000, 'W', true);
INSERT INTO territories VALUES ('NL', 'Netherlands', 16856620, 'W', true);
INSERT INTO territories VALUES ('BE', 'Belgium', 11198638, 'W', true);
INSERT INTO territories VALUES ('LU', 'Luxembourg', 549680, 'W', true);
INSERT INTO territories VALUES ('FR', 'France', 66616416, 'W', true);
INSERT INTO territories VALUES ('RU', 'Russia', 143900000, 'W', true);
INSERT INTO territories VALUES ('BY', 'Belarus', 9475100, 'W', true);
INSERT INTO territories VALUES ('UA', 'Ukraine', 44291413, 'W', true);
INSERT INTO territories VALUES ('SK', 'Slovakia', 5415949, 'W', true);
INSERT INTO territories VALUES ('CZ', 'Czech Republic', 10513209, 'W', true);
INSERT INTO territories VALUES ('ES', 'Spain', 46704314, 'W', true);
INSERT INTO territories VALUES ('PT', 'Portugal', 10427301, 'W', true);
INSERT INTO territories VALUES ('IT', 'Italy', 60782668, 'W', true);
INSERT INTO territories VALUES ('CH', 'Switzerland', 8183800, 'W', true);
INSERT INTO territories VALUES ('AT', 'Austria', 8572895, 'W', true);
INSERT INTO territories VALUES ('HU', 'Hungary', 9877365, 'W', true);
INSERT INTO territories VALUES ('RO', 'Romania', 19942642, 'W', true);
INSERT INTO territories VALUES ('MD', 'Moldova', 2913281, 'W', true);
INSERT INTO territories VALUES ('BG', 'Bulgaria', 7364570, 'W', true);
INSERT INTO territories VALUES ('RS', 'Republic of Serbia', 7209764, 'W', true);
INSERT INTO territories VALUES ('ME', 'Montenegro', 703208, 'W', true);
INSERT INTO territories VALUES ('MK', 'Macedonia', 2058539, 'W', true);
INSERT INTO territories VALUES ('GR', 'Greece', 10816286, 'W', true);
INSERT INTO territories VALUES ('AL', 'Albania', 3020209, 'W', true);
INSERT INTO territories VALUES ('BA', 'Bosnia and Herzegovina', 3871643, 'W', true);
INSERT INTO territories VALUES ('HR', 'Croatia', 4284889, 'W', true);
INSERT INTO territories VALUES ('SI', 'Slovenia', 2061085, 'W', true);
INSERT INTO territories VALUES ('CY', 'Cyprus', 1117000, 'W', true);

INSERT INTO territories VALUES ('PLPK', 'Podkarpackie', 2101732, 'PL', false);
INSERT INTO territories VALUES ('PLMZ', 'Mazowieckie', 5164612, 'PL', false);
INSERT INTO territories VALUES ('PLLU', 'Lubelskie', 2175251, 'PL', false);
INSERT INTO territories VALUES ('PLPD', 'Podlaskie', 1197610, 'PL', false);
INSERT INTO territories VALUES ('PLSW', 'Świętokrzyskie', 1281796, 'PL', false);

INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rzeszów', 182028, 'PLPK', 50.33, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Przemyśl', 64276, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Stalowa Wola', 64189, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mielec', 61238, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tarnobrzeg', 48558, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Krosno', 47307, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Dębica', 47180, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jarosław', 39426, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sanok', 39375, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jasło', 36641, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łańcut', 18143, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Przeworsk', 15915, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ropczyce', 15655, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nisko', 15479, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Leżajsk', 14448, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lubaczów', 12552, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nowa Dęba', 11507, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ustrzyki Dolne', 9521, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kolbuszowa', 9341, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Strzyżów', 8930, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Brzozów', 7631, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sędziszów Małopolski', 7481, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rudnik nad Sanem', 6883, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nowa Sarzyna', 6287, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Dynów', 6195, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Głogów Małopolski', 6014, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Boguchwała', 5796, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jedlicze', 5782, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lesko', 5674, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Radymno', 5528, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zagórz', 5100, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pilzno', 4762, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sokołów Małopolski', 4028, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rymanów', 3753, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pruchnik', 3716, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tyczyn', 3602, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kańczuga', 3242, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Oleszyce', 3108, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Radomyśl Wielki', 3079, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Brzostek', 2697, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sieniawa', 2198, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Dukla', 2194, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Błażowa', 2182, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Narol', 2100, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Cieszanów', 1962, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Iwonicz-Zdrój', 1904, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Przecław', 1682, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Baranów Sandomierski', 1508, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ulanów', 1482, 'PLPK', 52.02, 22);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kołaczyce', 1447, 'PLPK', 52.02, 22);

INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lublin', 343598, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Chełm', 65481, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zamość', 65255, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Biała Podlaska', 57658, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Puławy', 49100, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Świdnik', 40225, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kraśnik', 35788, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łuków', 30757, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Biłgoraj', 27169, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lubartów', 22543, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łęczna', 19926, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tomaszów Lubelski', 19983, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Krasnystaw', 19539, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Hrubieszów', 18585, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Dęblin', 17057, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Międzyrzec Podlaski', 17117, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Radzyń Podlaski', 16188, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Włodawa', 13643, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Janów Lubelski', 12092, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Parczew', 10953, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ryki', 9955, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Poniatowa', 9646, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Opole Lubelskie', 8860, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Bełżyce', 6765, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Terespol', 5815, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Bychawa', 5178, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Szczebrzeszyn', 5245, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rejowiec Fabryczny', 4515, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nałęczów', 4033, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tarnogród', 3464, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kock', 3412, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zwierzyniec', 3347, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Krasnobród', 3117, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Stoczek Łukowski', 2700, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Piaski', 2672, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kazimierz Dolny', 2607, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Annopol', 2664, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Józefów', 2520, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tyszowce', 2171, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łaszczów', 2187, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ostrów Lubelski', 2207, 'PLLU', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Frampol', 1482, 'PLLU', 0, 0);

INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Vilnius', 529022, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kaunas', 304012, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Klaipėda', 157305, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Šiauliai', 105610, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Panevėžys', 96328, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Alytus', 56357, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Marijampolė', 38846, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mažeikiai', 35997, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jonava', 29353, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Utena', 27484, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kėdainiai', 25654, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Telšiai', 24295, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tauragė', 23516, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ukmergė', 22510, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Visaginas', 20532, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Plungė', 19153, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kretinga', 18816, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Šilutė', 17002, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Radviliškis', 16527, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Palanga', 15358, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Gargždai', 14937, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Druskininkai', 13762, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rokiškis', 13637, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Biržai', 11666, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Elektrėnai', 11592, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kuršėnai', 11409, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Vilkaviškis', 11038, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Raseiniai', 10890, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jurbarkas', 10878, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Garliava', 10671, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lentvaris', 10610, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Grigiškės', 10394, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Anykščiai', 9968, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Prienai', 9394, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Joniškis', 9309, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Varėna', 8974, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kelmė', 8828, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Naujoji Akmenė', 8532, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kaišiadorys', 8243, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pasvalys', 7138, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kupiškis', 6931, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zarasai', 6779, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Šalčininkai', 6573, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kazlų Rūda', 6342, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Molėtai', 6204, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Širvintos', 6075, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Skuodas', 6032, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Šakiai', 5773, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pabradė', 5723, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ignalina', 5702, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Šilalė', 5454, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Švenčionėliai', 5328, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kybartai', 5223, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nemenčinė', 4933, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pakruojis', 4919, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Švenčionys', 4845, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Trakai', 4837, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Vievis', 4662, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lazdijai', 4357, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kalvarija', 4349, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rietavas', 3574, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Žiežmariai', 3453, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Eišiškės', 3276, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ariogala', 2939, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Neringa', 2752, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Šeduva', 2662, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Akmenė', 2504, 'LT', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Birštonas', 2475, 'LT', 0, 0);

INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Białystok', 295282, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Suwałki', 69317, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łomża', 62711, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Augustów', 30610, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Bielsk Podlaski', 26336, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zambrów', 22451, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Grajewo', 22246, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Hajnówka', 21559, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sokółka', 18765, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łapy', 16005, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Siemiatycze', 14766, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kolno', 10579, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Wasilków', 10511, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mońki', 10352, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Czarna Białostocka', 9671, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Wysokie Mazowieckie', 9503, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Dąbrowa Białostocka', 5874, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Choroszcz', 5782, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sejny', 5650, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ciechanowiec', 4840, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Supraśl', 4679, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Brańsk', 3867, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Szczuczyn', 3489, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Michałowo', 3177, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Knyszyn', 2850, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Czyżew', 2636, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Krynki', 2511, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zabłudów', 2495, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lipsk', 2443, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Stawiski', 2379, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Szepietowo', 2282, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Suchowola', 2274, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nowogród', 2197, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Drohiczyn', 2125, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tykocin', 2014, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Goniądz', 1900, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jedwabne', 1717, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Rajgród', 1626, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kleszczele', 1345, 'PLPD', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Suraż', 1008 , 'PLPD', 0, 0);

INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kielce', 199870, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ostrowiec Świętokrzyski', 72277, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Starachowice', 51158, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Skarżysko-Kamienna', 47538, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sandomierz', 24552, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Końskie', 20281, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Busko-Zdrój', 16694, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Jędrzejów', 15851, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Staszów', 15384, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pińczów', 11306, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Włoszczowa', 10545, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Suchedniów', 8714, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Połaniec', 8347, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Opatów', 6767, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sędziszów', 6707, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Stąporków', 5996, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kazimierza Wielka', 5748, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ożarów', 4730, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Chęciny', 4438, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Małogoszcz', 3929, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Chmielnik', 3921, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ćmielów', 3157, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kunów', 3098, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Daleszyce', 2911, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Wąchock', 2865, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Koprzywnica', 2575, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Bodzentyn', 2274, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Osiek', 2004, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zawichost', 1861, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Skalbmierz', 1315, 'PLSW', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Działoszyce', 981 , 'PLSW', 0, 0);

INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Warszawa', 1724404, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Radom', 218466, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Płock', 122815, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Siedlce', 76347, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pruszków', 59570, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Legionowo', 54231, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ostrołęka', 52917, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Otwock', 45044, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Piaseczno', 44869, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ciechanów', 44797, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Żyrardów', 41096, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mińsk Mazowiecki', 39880, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Wołomin', 37505, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sochaczew', 37480, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ząbki', 31884, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mława', 30880, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Grodzisk Mazowiecki', 29907, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Marki', 29032, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nowy Dwór Mazowiecki', 28287, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Wyszków', 27222, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Piastów', 22826, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ostrów Mazowiecka', 22796, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Płońsk', 22494, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kobyłka', 20855, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Józefów', 19914, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pionki', 19382, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sulejówek', 19311, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pułtusk', 19228, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Gostynin', 19092, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sokołów Podlaski', 18720, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Sierpc', 18491, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kozienice', 18277, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zielonka', 17398, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Konstancin-Jeziorna', 17391, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Przasnysz', 17326, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Garwolin', 17146, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łomianki', 16639, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Grójec', 16453, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Milanówek', 16410, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Brwinów', 13090, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Węgrów', 12796, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Błonie', 12570, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Szydłowiec', 12064, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Warka', 11727, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Góra Kalwaria', 11685, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Radzymin', 11378, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Ożarów Mazowiecki', 10561, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Karczew', 10160, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Maków Mazowiecki', 10118, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Żuromin', 9022, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zwoleń', 8112, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tłuszcz', 7989, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nasielsk', 7650, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Białobrzegi', 7132, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łosice', 7099, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łochów', 6786, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mszczonów', 6460, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Przysucha', 6146, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Lipsko', 5816, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Iłża', 5054, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Łaskarzew', 4914, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Raciąż', 4662, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Pilawa', 4438, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Skaryszew', 4308, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Gąbin', 4142, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Serock', 4130, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Tarczyn', 4127, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Żelechów', 4081, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Nowe Miasto nad Pilicą', 3920, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Podkowa Leśna', 3869, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Halinów', 3654, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Zakroczym', 3241, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Myszyniec', 3170, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Glinojeck', 3150, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Drobin', 2992, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Chorzele', 2939, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kałuszyn', 2934, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Różan', 2730, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Wyszogród', 2709, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mogielnica', 2386, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Kosów Lacki', 2187, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Brok', 1977, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Bieżuń', 1911, 'PLMZ', 0, 0);
INSERT INTO cities(name, population, container, latitude, longitude) VALUES ('Mordy', 1819, 'PLMZ', 0, 0);

# --- !Downs
TRUNCATE TABLE cities CASCADE;
TRUNCATE TABLE territories CASCADE;