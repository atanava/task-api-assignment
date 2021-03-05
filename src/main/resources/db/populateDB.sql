DELETE FROM tasks;
ALTER SEQUENCE GLOBAL_SEQ RESTART WITH 1;

INSERT INTO tasks (description, created, modified, completed)
VALUES ('Do work 1', '2021-03-03 10:00:00', null, true),
       ('Do work 2', '2021-03-03 12:00:00', '2021-03-03 23:59:59', false),
       ('Do work 3', '2021-03-04 09:00:00', null, true),
       ('Do work 4', '2021-03-04 10:00:00', '2021-03-04 12:00:00', false),
       ('Do work 5', '2021-03-04 14:25:10', null, false);