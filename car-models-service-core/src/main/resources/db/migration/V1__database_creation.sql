CREATE TABLE manufacturers(
  name VARCHAR(256) PRIMARY KEY
);

CREATE TABLE model_years(
  year_value TIMESTAMP PRIMARY KEY
);

CREATE TABLE categories(
  name VARCHAR(256) PRIMARY KEY
);

CREATE TABLE models(
  id VARCHAR PRIMARY KEY,
  name VARCHAR(256),
  model_year TIMESTAMP references model_years(year_value) ON UPDATE CASCADE NOT NULL,
  manufacturer_name VARCHAR(256) REFERENCES manufacturers(name) ON UPDATE CASCADE NOT NULL
);

CREATE TABLE model_category(
  model_id VARCHAR(256) REFERENCES models(id) ON UPDATE CASCADE NOT NULL,
  category_name VARCHAR(256) REFERENCES categories(name) ON UPDATE CASCADE NOT NULL
);