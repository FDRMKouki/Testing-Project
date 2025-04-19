package lesson.project.studentsmanagement.project.repository;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 受講生情報を扱うリポ。 全件検索や条件付き検索などができるようにする
 */
@Mapper
public interface StudentRepository {

  @Select("SELECT * FROM students")
  List<Student> searchStudent();

  @Select("SELECT * FROM students_courses")
  List<StudentsCourses> searchStudentCourse();

  //----生徒登録の実行----
  //コースを登録する
  @Insert(
      "INSERT INTO students_courses (student_id, course_name, start_datetime_at, predicted_complete_datetime_at) "
          + "VALUES (#{studentId}, #{courseName}, #{startDatetimeAt}, #{predictedCompleteDatetimeAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudentsCourses(StudentsCourses studentsCourses);

  //----生徒表示----
  //引数で受け取り、戻り値はvoidにする
  //students(id, name, ...) VALUES (...
  //         ↑かっこを省略すると全項目指定
  //idは自動採番でっせ
  @Insert(
      "INSERT INTO students (name, furigana, nickname, mail_address, region, age, gender, remark, is_deleted) "
          + "VALUES (#{name}, #{furigana}, #{nickname}, #{mailAddress}, #{region}, #{age}, #{gender}, #{remark}, 0)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudent(Student student);

  //Idで生徒取得
  @Select("SELECT * FROM students WHERE id = #{id}")
  Student findStudentById(String id);

  //こっちはコース情報
  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  List<StudentsCourses> findCoursesByStudentId(String studentId);

  //----生徒更新の実行----
  @Update("UPDATE students SET name = #{name}, furigana = #{furigana}, nickname = #{nickname}, " +
      "mail_address = #{mailAddress}, region = #{region}, age = #{age}, gender = #{gender}, remark = #{remark} "
      +
      "WHERE id = #{id}")
  void updateStudent(Student student);
}
