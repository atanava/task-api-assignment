DROP TABLE IF EXISTS tasks;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 1;

CREATE TABLE tasks
(
    id          INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    description VARCHAR                           NOT NULL,
    added       TIMESTAMP           DEFAULT now() NOT NULL,
    modified    TIMESTAMP,
    completed   TIMESTAMP,
    CONSTRAINT chk_modified CHECK (modified IS NULL OR modified > added),
    CONSTRAINT chk_completed CHECK (completed IS NULL OR (completed > added AND completed > modified))
)