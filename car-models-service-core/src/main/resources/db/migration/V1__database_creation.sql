CREATE
    TABLE
        manufacturers(
            name VARCHAR PRIMARY KEY
        );

CREATE
    TABLE
        model_names(
            name VARCHAR PRIMARY KEY
        );

CREATE
    TABLE
        categories(
            name VARCHAR PRIMARY KEY
        );

CREATE
    TABLE
        models(
            id VARCHAR PRIMARY KEY,
            model_year INTEGER NOT NULL,
            manufacturer_name VARCHAR REFERENCES manufacturers(name) ON
            UPDATE
                CASCADE NOT NULL,
                name VARCHAR REFERENCES model_names(name) ON
                UPDATE
                    CASCADE NOT NULL
        );

CREATE
    TABLE
        model_category(
            model_id VARCHAR REFERENCES models(id) ON
            UPDATE
                CASCADE NOT NULL,
                category_name VARCHAR REFERENCES categories(name) ON
                UPDATE
                    CASCADE NOT NULL
        );