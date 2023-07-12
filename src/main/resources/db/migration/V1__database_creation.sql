create table manufacturers(
	name varchar primary key
);

create table models(
	name varchar primary key
);

create table categories(
	name varchar primary key
);

create table vehicles(
	id varchar primary key,
	production_year integer,
	manufacturer_name varchar references manufacturers(name) on delete set null,
	model_name varchar references models(name) on delete set null,
);

create table vehicle_category(
	vehicle_id varchar references vehicles(id) not null,
	category_name varchar references categories(name) not null
);