package lesson.project.studentsmanagement.project.repository;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

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
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudent(Student student);

  /**
   * コースを登録する（IDは自動採番）。
   *
   * @param studentCourse コース情報
   */
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudentCourse(StudentCourse studentCourse);

  // ----------- Read -----------

  /**
   * 論理削除されていない全生徒を取得する。
   *
   * @return 生徒リスト
   */
  List<Student> searchStudent();

  /**
   * 全てのコース情報を取得する。
   *
   * @return コースリスト
   */

  List<StudentCourse> searchStudentCourseList();

  /**
   * 指定IDの生徒を取得する。
   *
   * @param id 生徒ID
   * @return 生徒情報
   */
  Student findStudentById(String id);

  /**
   * 指定生徒IDに紐づく全コース情報を取得する。
   *
   * @param studentId 生徒ID
   * @return コース情報のリスト
   */
  List<StudentCourse> searchStudentCourse(String studentId);

  // ----------- Update -----------

  /**
   * 生徒情報を更新する。
   *
   * @param student 更新後の生徒情報
   */
  void updateStudent(Student student);

  /**
   * コース情報の更新。 名前のみ更新する。
   *
   * @param studentCourse 更新後のコース情報
   */
  void updateStudentCourse(StudentCourse studentCourse);

  // ----------- Delete -----------

  /**
   * 指定IDの生徒を論理削除する（is_deleted = 1）。
   *
   * @param student 対象生徒
   */
  void logicalDeleteStudent(Student student);
}
