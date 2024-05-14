delete from model_category;
delete from categories;
delete from models;
delete from model_years;
delete from manufacturers;

insert into categories(name) values('Sedan');
insert into categories(name) values('Coupe');
insert into model_years(year_value) values('2020-12-31');
insert into manufacturers(name) values('Audi');
insert into manufacturers(name) values('Ford');
insert into models(id, model_year, manufacturer_name, name)
  values ('52096834-48af-41d1-b422-93600eff629a', '2020-12-31', 'Audi', 'A7');
insert into model_category (model_id, category_name) values ('52096834-48af-41d1-b422-93600eff629a', 'Sedan');
