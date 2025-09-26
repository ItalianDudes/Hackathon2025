-- TABLE: Terrains
CREATE TABLE IF NOT EXISTS terrains (
       id INTEGER NOT NULL PRIMARY KEY,
       name TEXT NOT NULL,
       owner TEXT NOT NULL,
       dimension TEXT NOT NULL
);

-- TABLE: Sectors
CREATE TABLE IF NOT EXISTS sectors (
       id INTEGER NOT NULL PRIMARY KEY,
       terrain_id INTEGER NOT NULL REFERENCES terrains(id)
       ON UPDATE CASCADE
       ON DELETE CASCADE,
       name TEXT NOT NULL,
       dimension TEXT
);

-- Table: Sensors
CREATE TABLE IF NOT EXISTS sensors (
    id INTEGER NOT NULL PRIMARY KEY,
    sector_id INTEGER NOT NULL REFERENCES sectors(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
    name TEXT NOT NULL,
    ip TEXT NOT NULL,
    port INTEGER NOT NULL,
    output_type TEXT NOT NULL,
    unit_of_measurement TEXT NOT NULL,
    preset TEXT
);

-- Table: Actuators
CREATE TABLE IF NOT EXISTS actuators (
    id INTEGER NOT NULL PRIMARY KEY,
    sector_id INTEGER NOT NULL REFERENCES sectors(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
    name TEXT NOT NULL,
    ip TEXT NOT NULL,
    port INTEGER NOT NULL,
    input_type TEXT NOT NULL,
    value TEXT NOT NULL,
    min_value TEXT NOT NULL,
    max_value TEXT NOT NULL,
    allow_override INTEGER NOT NULL DEFAULT 0,
    is_override_active INTEGER NOT NULL DEFAULT 0
);