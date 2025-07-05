CREATE TABLE students (
id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  furigana VARCHAR(50) NOT NULL,
  nickname VARCHAR(20),
  mail_address VARCHAR(30) NOT NULL,
  region VARCHAR(20),
  age INT,
  gender VARCHAR(10),
  remark VARCHAR(50),
  is_deleted INT
);

CREATE TABLE students_courses (
id INT PRIMARY KEY AUTO_INCREMENT,
  student_id INT NOT NULL,
  course_name VARCHAR(50) NOT NULL,
  start_datetime_at TIMESTAMP,
  predicted_complete_datetime_at TIMESTAMP
);
