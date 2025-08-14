package lesson.project.studentsmanagement.project.repository;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lesson.project.studentsmanagement.project.data.CourseStatus;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.Transactional;


@MybatisTest
@EnableCaching
@Transactional
class StudentRepositoryTest {

  @Autowired
  private SqlSession sqlSession;

  @Autowired
  private StudentRepository sut;

  // ----------- Create -----------
  @Test
  void 受講生が登録できることのテスト() {
    Student student = new Student(
        null,                    // ID は登録時 null
        "登録太郎",
        "トウロクタロウ",
        "とろたろ",
        "taro@example.com",
        "関東",
        25,
        "male",
        "テスト登録",
        false
    );
    sut.registerStudent(student);
    //リポとうろくはvoidで返しがない=継承できない ので増加後のリストを取得させる作戦 論理削除されてない受講生を読み取ることに注意
    List<Student> actual = sut.searchStudent();
    //5+1=6
    assertThat(actual.size()).isEqualTo(6);
    Student inserted = actual.stream()
        .filter(s -> "登録太郎".equals(s.getName()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("登録した生徒が見つかりません"));

    // 登録した内容の検証 コンストラクタを使うとassertThatが一つで済む
    assertThat(inserted).isEqualTo(new Student(
        inserted.getId(), // 自動採番されたIDを利用
        "登録太郎",
        "トウロクタロウ",
        "とろたろ",
        "taro@example.com",
        "関東",
        25,
        "male",
        "テスト登録",
        false
    ));
  }

  @Test
  void コースが登録できることのテスト() {
    StudentCourse course = new StudentCourse(8L,
        2L,//結びつく生徒IDであって、コースのIDではない
        "Javava",
        LocalDateTime.of(2025, 7, 1, 10, 0),
        LocalDateTime.of(2025, 9, 30, 18, 0)
    );
    sut.registerStudentCourse(course);
    // 検証：登録後のリストを取得
    List<StudentCourse> actual = sut.searchStudentCourseList();

    // 検証1：件数が1件増えている（7+1=8)
    assertThat(actual.size()).isEqualTo(8);

    // 検証2：登録したコースの内容を確認
    StudentCourse inserted = actual.stream()
        .filter(c -> "Javava".equals(c.getCourseName()) && c.getStudentId() == 2L)
        .findFirst()
        .orElseThrow(() -> new AssertionError("登録したコースが見つかりません"));

    assertThat(inserted).isEqualTo(course);
  }

  @Test
  void 申込状況が登録できることのテスト() {
    // コースを登録
    StudentCourse newCourse = new StudentCourse(null, // id (auto increment)
        1L, // student_id (既存の生徒ID)
        "Python",
        LocalDateTime.of(2025, 8, 1, 10, 0),
        LocalDateTime.of(2025, 10, 30, 18, 0)
    );
    sut.registerStudentCourse(newCourse);

    // 登録後のコースIDを取得（Long に変換）
    Long courseId = newCourse.getId() != null ? newCourse.getId().longValue() : null;
    assertThat(courseId).isNotNull();

    // 申込状況を登録
    CourseStatus status = new CourseStatus(courseId, 2); // 2 = 本申込など
    sut.registerCourseStatus(status);

    // 検証
    List<CourseStatus> statusList = sut.findCourseStatusByCourseId(String.valueOf(courseId));
    assertThat(statusList).isNotEmpty();
    assertThat(statusList.get(0).getAppStatus()).isEqualTo(2);
  }


  // ----------- Read -----------
  @Test
  void 論理削除されていない受講生の全件検索ができることのテスト() {
    // 論理削除されていない生徒のみ取得される
    List<Student> actual = sut.searchStudent();
    //5人いるよね?
    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void コースの全件検索ができることのテスト() {
    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(7);
  }

  @Test
  void ID指定で生徒を取得できることのテスト() {
    //data.sqlの奴と同じ
    Student expected = new Student(
        2L,
        "Slco",
        "Silico",
        "Si",
        "slco@example",
        "here",
        20,
        "female",
        "slconoremark",
        false
    );

    // 実際に取得
    Student actual = sut.findStudentById("2");

    //全フィールドがっちり一致を目指す
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void 生徒IDに紐づくコース情報が取得できることのテスト() {
    //Pは2つもコースがある
    List<StudentCourse> actual = sut.findStudentCourseByStudentId("3");

    StudentCourse expected1 = new StudentCourse(3L,
        3L, "Art",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 8, 30, 18, 0)
    );

    StudentCourse expected2 = new StudentCourse(4L,
        3L, "AWS",
        LocalDateTime.of(2025, 7, 1, 10, 0),
        LocalDateTime.of(2025, 9, 30, 18, 0)
    );

    assertThat(actual).contains(expected1, expected2);
  }

  @Test
  void 複数生徒IDでコース情報を取得できることのテスト() {
    List<StudentCourse> courses = sut.findStudentCoursesByStudentIds(List.of("1", "3"));
    List<Long> studentIds = courses.stream()
        .map(StudentCourse::getStudentId)
        .toList();

    assertThat(studentIds).contains(1L, 3L);
    assertThat(courses).isNotEmpty();
  }

  @Test
  void 複数コースIDで申込状況を取得できることのテスト() {
    List<Long> courseIds = List.of(1L, 4L, 6L);
    List<CourseStatus> statusList = sut.findCourseStatusesByCourseIds(
        courseIds.stream().map(String::valueOf).toList()
    );

    assertThat(statusList).hasSize(courseIds.size());

    List<Long> fetchedCourseIds = statusList.stream()
        .map(CourseStatus::getCourseId)
        .toList();

    assertThat(fetchedCourseIds).containsExactlyInAnyOrderElementsOf(courseIds);

    statusList.forEach(status -> assertThat(status.getAppStatus()).isPositive());
  }

  @Test
  void 条件検索で生徒を取得できることのテスト() {
    Map<String, Object> params = new HashMap<>();
    params.put("name", "Cabn");
    params.put("furigana", null);
    params.put("mailAddress", null);
    params.put("deleted", 0); // H2では0/1
    List<Student> actual = sut.searchStudentsByConditions(params);

    assertThat(actual).isNotEmpty();

    Student expected = new Student(
        1L,
        "Cabn",
        "Carbn",
        "C",
        "cabn@example",
        "everywhere",
        20,
        "male",
        "cabnnoremark",
        false
    );

    // リストに期待値が含まれることを確認
    assertThat(actual).anyMatch(s -> s.equals(expected));
  }

  @Test
  void 存在しない生徒IDで取得した場合はnullになることのテスト() {
    Student s = sut.findStudentById("999");
    assertThat(s).isNull();
  }

  @Test
  void 存在しないコースIDで申込状況を取得すると空リストになることのテスト() {
    List<CourseStatus> list = sut.findCourseStatusByCourseId("999");
    assertThat(list).isEmpty();
  }

  // ----------- Update -----------

  @Test
  void 生徒情報が更新できることのテスト() {
    Student student = sut.findStudentById("1");
    student.setName("ダイヤ更新済み");
    sut.updateStudent(student);

    Student actual = sut.findStudentById("1");

    Student expected = new Student(
        1L,
        "ダイヤ更新済み",
        "Carbn",
        "C",
        "cabn@example",
        "everywhere",
        20,
        "male",
        "cabnnoremark",
        false
    );

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void コース情報が更新できることのテスト() {
    List<StudentCourse> courses = sut.findStudentCourseByStudentId("1");
    StudentCourse course = courses.get(0);
    course.setCourseName("更新Java");
    sut.updateStudentCourse(course);

    List<StudentCourse> actual = sut.findStudentCourseByStudentId("1");

    StudentCourse expected = new StudentCourse(8L,
        1L,
        "更新Java",
        LocalDateTime.of(2025, 4, 1, 10, 0),
        LocalDateTime.of(2025, 6, 30, 18, 0)
    );
    // IDは比較に含めないようequals()を定義していればOK
    assertThat(actual).contains(expected);
  }

  @Test
  void 申込状況が更新できることのテスト() {
    // 既存コースID
    Long courseId = 1L;
    // 登録済みの申込状況を確認
    List<CourseStatus> before = sut.findCourseStatusByCourseId(String.valueOf(courseId));
    assertThat(before.get(0).getAppStatus()).isEqualTo(1);

    // 更新
    sut.updateCourseStatusById(before.get(0).getId(), 3); // 受講中に変更

    // 検証
    List<CourseStatus> after = sut.findCourseStatusByCourseId(String.valueOf(courseId));
    assertThat(after.get(0).getAppStatus()).isEqualTo(3);
  }

  // ----------- Delete -----------

  @Test
  void 生徒を論理削除できることのテスト() {
    long studentId = 1L;

    // 削除前に生徒が存在することを確認
    Student before = sut.findStudentById(String.valueOf(studentId));
    assertThat(before).isNotNull();
    assertThat(before.isDeleted()).isFalse();

    // 実行
    int updatedCount = sut.logicalDeleteStudent(studentId);
    assertThat(updatedCount).isGreaterThan(0);

    // キャッシュクリア
    sqlSession.clearCache();

    // 検証
    Student after = sut.findStudentById(String.valueOf(studentId));
    System.out.println("isDeleted flag from DB after update: " + after.isDeleted());
    assertThat(after).isNotNull();
    assertThat(after.isDeleted()).isTrue();
  }

  @CacheEvict(value = "student", key = "#studentId")
  void clearStudentCache(String studentId) {
    // 実際の削除は @CacheEvict に任せる
  }
}