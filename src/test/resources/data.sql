insert into categories(name) values('Sedan');
insert into models(name) values('A7');
insert into manufacturers(name) values('Audi');
insert into vehicles(id, production_year) values ('1', 2020); 
insert into vehicles(id, production_year, manufacturer_name, model_name) values ('2', 2020, 'Audi', 'A7'); 
insert into vehicle_category (vehicle_id, category_name) values ('2', 'Sedan');
