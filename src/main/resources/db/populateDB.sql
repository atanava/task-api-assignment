DELETE FROM tasks;
ALTER SEQUENCE GLOBAL_SEQ RESTART WITH 1;

INSERT INTO tasks (description, added, modified, completed)
VALUES ('Do work 1', '2021-03-03 10:00:00', null, '2021-03-04 10:00:00'),
       ('Do work 2', '2021-03-03 12:00:00', '2021-03-03 23:59:59', null),
       ('Do work 3', '2021-03-04 09:00:00', null, '2021-03-05 10:00:00'),
       ('Do work 4', '2021-03-04 10:00:00', '2021-03-04 12:00:00', null),
       ('Do work 5', '2021-03-04 14:25:10', null, null);