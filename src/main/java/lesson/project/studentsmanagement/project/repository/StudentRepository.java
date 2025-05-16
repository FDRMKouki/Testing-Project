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
 * 生徒・コース情報に対するDB操作（登録・検索・更新・削除）を担当するリポジトリ。
 */
@Mapper
public interface StudentRepository {

  // ----------- Create -----------

  /**
   * 生徒を登録する（IDは自動採番）。
   *
   * @param student 生徒情報
   */
  @Insert("""
      INSERT INTO students (
        name, furigana, nickname, mail_address, region, age, gender, remark, is_deleted
      ) VALUES (
        #{name}, #{furigana}, #{nickname}, #{mailAddress}, #{region},
        #{age}, #{gender}, #{remark}, 0
      )
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudent(Student student);

  /**
   * コースを登録する（IDは自動採番）。
   *
   * @param studentsCourses コース情報
   */
  @Insert("""
      INSERT INTO students_courses (
        student_id, course_name, start_datetime_at, predicted_complete_datetime_at
      ) VALUES (
        #{studentId}, #{courseName}, #{startDatetimeAt}, #{predictedCompleteDatetimeAt}
      )
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudentsCourses(StudentsCourses studentsCourses);

  // ----------- Read -----------

  /**
   * 論理削除されていない全生徒を取得する。
   *
   * @return 生徒リスト
   */
  @Select("SELECT * FROM students WHERE is_deleted = 0")
  List<Student> searchStudent();

  /**
   * 全てのコース情報を取得する。
   *
   * @return コースリスト
   */
  @Select("SELECT * FROM students_courses")
  List<StudentsCourses> searchStudentCourse();

  /**
   * 指定IDの生徒を取得する。
   *
   * @param id 生徒ID
   * @return 生徒情報
   */
  @Select("SELECT * FROM students WHERE id = #{id}")
  Student findStudentById(String id);

  /**
   * 指定生徒IDに紐づく全コース情報を取得する。
   *
   * @param studentId 生徒ID
   * @return コース情報のリスト
   */
  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  List<StudentsCourses> findCoursesByStudentId(String studentId);

  // ----------- Update -----------

  /**
   * 生徒情報を更新する。
   *
   * @param student 更新後の生徒情報
   */
  @Update("""
      UPDATE students SET
        name = #{name},
        furigana = #{furigana},
        nickname = #{nickname},
        mail_address = #{mailAddress},
        region = #{region},
        age = #{age},
        gender = #{gender},
        remark = #{remark}
      WHERE id = #{id}
      """)
  void updateStudent(Student student);

  /**
   * コース名のみ更新する。
   *
   * @param studentsCourses 更新後のコース情報
   */
  @Update("""
      UPDATE students_courses SET
        course_name = #{courseName}
      WHERE id = #{id}
      """)
  void updateStudentsCourses(StudentsCourses studentsCourses);

  // ----------- Delete -----------

  /**
   * 指定IDの生徒を論理削除する（is_deleted = 1）。
   *
   * @param student 対象生徒
   */
  @Update("UPDATE students SET is_deleted = 1 WHERE id = #{id}")
  void logicalDeleteStudent(Student student);
}
