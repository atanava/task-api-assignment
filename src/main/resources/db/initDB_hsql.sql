DROP TABLE tasks IF EXISTS;
DROP SEQUENCE GLOBAL_SEQ IF EXISTS;

CREATE SEQUENCE GLOBAL_SEQ AS INTEGER START WITH 1;

CREATE TABLE tasks
(
    id          INTEGER GENERATED BY DEFAULT AS SEQUENCE GLOBAL_SEQ PRIMARY KEY,
    description VARCHAR(255)            NOT NULL,
    added     TIMESTAMP DEFAULT now() NOT NULL,
    modified    TIMESTAMP,
    completed   TIMESTAMP,
    CONSTRAINT chk_modified CHECK (modified IS NULL OR modified > added),
    CONSTRAINT chk_completed CHECK (completed IS NULL OR (completed > added AND completed > modified))
);