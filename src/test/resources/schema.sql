CREATE TABLE students (
id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50),
  furigana VARCHAR(50),
  nickname VARCHAR(20),
  mail_address VARCHAR(30),
  region VARCHAR(20),
  age INT,
  gender VARCHAR(10),
  remark VARCHAR(50),
  is_deleted INT
);

CREATE TABLE students_courses (
id INT PRIMARY KEY AUTO_INCREMENT,
  student_id INT,
  course_name VARCHAR(50),
  start_datetime_at TIMESTAMP,
  predicted_complete_datetime_at TIMESTAMP
);
