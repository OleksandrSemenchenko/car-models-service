create table manufacturers(
    name varchar primary key
);

create table model_names(
    name varchar primary key
);

create table categories(
    name varchar primary key
);

create table models(
    id varchar primary key,
    model_year integer not null,
    manufacturer_name varchar references manufacturers(name) on update cascade not null,
    name varchar references model_names(name) on update cascade not null
);

create table model_category(
    model_id varchar references models(id) on update cascade not null,
    category_name varchar references categories(name) on update cascade not null
);