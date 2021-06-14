CREATE TABLE IF NOT EXISTS t_cellar (
    ce_id SERIAL PRIMARY KEY,
    ce_name VARCHAR(64) NOT NULL,
    ce_friendly_name VARCHAR(64) NOT NULL,
    ce_description VARCHAR(256),
    ce_created_on TIMESTAMP,
    ce_modified_on TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_lookup_value (
    lv_id SERIAL PRIMARY KEY,
    lv_entity_type VARCHAR(64) NOT NULL,
    lv_entity_id NUMBER,
    lv_value_type VARCHAR(64) NOT NULL,
    lv_value VARCHAR(64) NOT NULL
);
