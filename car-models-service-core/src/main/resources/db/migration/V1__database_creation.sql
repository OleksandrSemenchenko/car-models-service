CREATE TABLE manufacturers(
  name VARCHAR PRIMARY KEY
);

CREATE TABLE model_years(
  year_value INTEGER PRIMARY KEY 
);

CREATE TABLE categories(
  name VARCHAR PRIMARY KEY
);

CREATE TABLE models(
  id VARCHAR PRIMARY KEY,
  model_year INTEGER references model_years(year_value) ON UPDATE CASCADE NOT NULL,
  manufacturer_name VARCHAR REFERENCES manufacturers(name) ON UPDATE CASCADE NOT NULL
);

CREATE TABLE model_category(
  model_id VARCHAR REFERENCES models(id) ON UPDATE CASCADE NOT NULL,
  category_name VARCHAR REFERENCES categories(name) ON UPDATE CASCADE NOT NULL
);