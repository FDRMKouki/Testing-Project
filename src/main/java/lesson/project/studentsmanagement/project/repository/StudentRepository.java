package lesson.project.studentsmanagement.project.repository;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 受講生情報を扱うリポ。 全件検索や条件付き検索などができるようにする
 */
@Mapper
public interface StudentRepository {

  /**
   * 全件検索する
   *
   * @return　全件検索結果
   */

  @Select("SELECT * FROM students")
  List<Student> searchStudent();

  @Select("SELECT * FROM students_courses")
  List<StudentsCourses> searchStudentCourse();

  //引数で受け取り、戻り値はvoidにする
  //students(id, name, ...) VALUES (...
  //         ↑かっこを省略すると全項目指定
  @Insert(
      "INSERT INTO students (id, name, furigana, nickname, mail_address, region, age, gender, remark, is_deleted) "
          +
          "VALUES (#{id}, #{name}, #{furigana}, #{nickname}, #{mailAddress}, #{region}, #{age}, #{gender}, #{remark}, 0)")
  void registerStudent(Student student);

  //コースを登録する
  @Insert(
      "INSERT INTO students_courses (student_id, course_name, start_datetime_at, predicted_complete_datetime_at) "
          + "VALUES (#{studentId}, #{courseName}, #{startDatetimeAt}, #{predictedCompleteDatetimeAt})")
  void insertCourse(StudentsCourses studentsCourses);

}
