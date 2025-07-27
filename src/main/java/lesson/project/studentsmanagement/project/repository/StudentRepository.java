package lesson.project.studentsmanagement.project.repository;

import java.util.List;
import lesson.project.studentsmanagement.project.data.CourseStatus;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

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

  /**
   * コース申込状況を登録する（IDは自動採番）。
   *
   * @param status 登録する申込状況
   */
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerCourseStatus(CourseStatus status);

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
   * 全てのコース情報を取得する。
   *
   * @return コースリスト
   */

  List<CourseStatus> searchCourseStatusList();

  /**
   * 指定IDの生徒を取得する。
   *
   * @param id 生徒ID
   * @return 生徒情報
   */
  Student findStudentById(String id);

  /**
   * 指定生徒IDに紐づく全コース情報を取得する。(単一の生徒向け)
   *
   * @param studentId 生徒ID
   * @return コース情報のリスト
   */
  List<StudentCourse> findStudentCourseByStudentId(String studentId);

  /**
   * 指定コースIDに紐づく全コース申込状況を取得する。(単一の生徒向け)
   *
   * @param courseId 生徒ID
   * @return コース情報のリスト
   */
  List<CourseStatus> findCourseStatusByCourseId(String courseId);

  /**
   * 条件で生徒を検索（名前・ふりがな・メールアドレスの部分一致）。
   *
   * @param name        名前（部分一致／null可）
   * @param furigana    フリガナ（部分一致／null可）
   * @param mailAddress メールアドレス（部分一致／null可）
   * @return 条件に一致した生徒リスト
   */
  List<Student> searchStudentsByConditions(
      @Param("name") String name,
      @Param("furigana") String furigana,
      @Param("mailAddress") String mailAddress
  );

  /**
   * 指定された生徒IDのリストに紐づくすべてのコース情報を取得する。(複数の生徒向け)
   *
   * @param studentIds 生徒IDリスト
   * @return 該当するStudentCourseリスト
   */
  List<StudentCourse> findStudentCoursesByStudentIds(@Param("studentIds") List<String> studentIds);

  /**
   * 指定されたコースIDリストに紐づく全ての申込状況を取得する。(複数の生徒向け)
   *
   * @param courseIds コースIDリスト
   * @return 該当するCourseStatusリスト
   */
  List<CourseStatus> findCourseStatusesByCourseIds(@Param("courseIds") List<String> courseIds);
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

  /**
   * コース申込状況の更新。 状況のみ更新する。
   *
   * @param courseId  コースID
   * @param appStatus 新しい申込ステータス
   */
  void updateCourseStatus(@Param("courseId") String courseId, @Param("appStatus") int appStatus);

  /**
   * IDごとのコース申込状況の更新。 状況のみ更新する。
   *
   * @param id        コースID
   * @param appStatus 新しい申込ステータス
   */
  void updateCourseStatusById(Long id, Integer appStatus);

  // ----------- Delete -----------

  /**
   * 指定IDの生徒を論理削除する（is_deleted = 1）。
   *
   * @param student 対象生徒
   */
  void logicalDeleteStudent(Student student);
}
