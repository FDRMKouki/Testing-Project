-- 受講生
INSERT INTO students (  id, name, furigana, nickname, mail_address, region, age, gender, remark, is_deleted) VALUES
(  1, 'Cabn', 'Carbn', 'C', 'cabn@example', 'everywhere', 20, 'male', 'cabnnoremark', 0),
(  2, 'Slco', 'Silico', 'Si', 'slco@example', 'here', 20, 'female', 'slconoremark', 0),
(  3, 'Phx', 'Phox', 'P', 'phx@example', 'rere', 14, 'female', 'phxnoremark', 0),
(  4, 'Galum', 'Gallium', 'Ga', 'galum@example', 'e', 16, 'female', 'galumnoremark', 0),
(  5, 'Azns', 'Azens', 'As', 'azns@example', 'swe', 17, 'female', 'aznsnoremark', 0),
(  6, 'Im not here,', 'None', 'None', 'none@example', 'None', 0, 'None', 'This student is deleted', 1);
ALTER TABLE students ALTER COLUMN id RESTART WITH 7;
--受講生コース
INSERT INTO students_courses ( id, student_id, course_name, start_datetime_at, predicted_complete_datetime_at) VALUES
(1, 1, 'Java', '2025-04-01 10:00:00', '2025-06-30 18:00:00'),
(2, 2, 'Art', '2025-03-01 10:00:00', '2025-05-30 18:00:00'),
(3, 3, 'Art', '2025-06-01 10:00:00', '2025-08-30 18:00:00'),
(4, 3, 'AWS', '2025-07-01 10:00:00', '2025-09-30 18:00:00'),
(5, 4, 'Java', '2025-01-01 10:00:00', '2025-03-30 18:00:00'),
(6, 5, 'Art', '2025-10-01 10:00:00', '2025-12-30 18:00:00'),
(7, 5, 'Java', '2025-12-01 10:00:00', '2025-01-30 18:00:00');
