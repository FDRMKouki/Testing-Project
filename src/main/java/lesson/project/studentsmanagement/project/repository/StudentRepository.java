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
 * 受講生、コースの情報のテーブルと紐づくリポ。全件検索や条件付き検索などをするコマンドがある
 */
@Mapper
public interface StudentRepository {

  //生徒の登録 CREATE
//---------------

  /**
   * 生徒の取得 引数で受け取り、戻り値はvoidにする students(id, name, ...) VALUES (... students次のかっこを省略すると全項目指定
   * idは自動採番でっせ
   *
   * @param student 受講生
   */

  @Insert(
      "INSERT INTO students (name, furigana, nickname, mail_address, region, age, gender, remark, is_deleted) "
          + "VALUES (#{name}, #{furigana}, #{nickname}, #{mailAddress}, #{region}, #{age}, #{gender}, #{remark}, 0)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudent(Student student);

  /**
   * コースを登録する
   *
   * @param studentsCourses 全コース情報
   */
  @Insert(
      "INSERT INTO students_courses (student_id, course_name, start_datetime_at, predicted_complete_datetime_at) "
          + "VALUES (#{studentId}, #{courseName}, #{startDatetimeAt}, #{predictedCompleteDatetimeAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudentsCourses(StudentsCourses studentsCourses);

  //生徒の表示系 READ
//---------------

  /**
   * 削除されていない全ての生徒情報を取得する全件検索。
   *
   * @return 受講生(全件)
   */
  @Select("SELECT * FROM students WHERE is_deleted = 0")
  List<Student> searchStudent();

  /**
   * (生徒コース情報を取得する)
   *
   * @return
   */
  @Select("SELECT * FROM students_courses")
  List<StudentsCourses> searchStudentCourse();

  /**
   * 特定のIDの生徒を取得する
   *
   * @param id 受講生ID
   * @return そのIDの受講生
   */
  @Select("SELECT * FROM students WHERE id = #{id}")
  Student findStudentById(String id);

  /**
   * 受講生IDに紐づくコース情報を取得する
   *
   * @param studentId 生徒のID
   * @return その生徒のIDに紐づく全てのコース情報
   */

  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  List<StudentsCourses> findCoursesByStudentId(String studentId);

  //生徒の更新 UPDATE
//---------------

  /**
   * 生徒の更新
   *
   * @param student 生徒情報(更新後)
   */
  @Update("UPDATE students SET name = #{name}, furigana = #{furigana}, nickname = #{nickname}, " +
      "mail_address = #{mailAddress}, region = #{region}, age = #{age}, gender = #{gender}, remark = #{remark} "
      +
      "WHERE id = #{id}")
  void updateStudent(Student student);

  /**
   * コース名だけ変更できるようにしている
   *
   * @param studentsCourses コース情報(更新後)
   */
  @Update("UPDATE students_courses SET course_name = #{courseName} " +
      "WHERE id = #{id}")
  void updateStudentsCourses(StudentsCourses studentsCourses);

  //生徒の削除 DELETE
//---------------

  /**
   * 特定のIDの受講生を(論理)削除する=特定のIDの受講生のisDeletedをtrueにする
   *
   * @param student 生徒情報
   */
  @Update("UPDATE students SET is_deleted = 1 WHERE id = #{id}")
  void logicalDeleteStudent(Student student);
}
