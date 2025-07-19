package lesson.project.studentsmanagement.project.repository;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

@MybatisTest
class StudentRepositoryTest {

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
    StudentCourse course = new StudentCourse(
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
    List<StudentCourse> actual = sut.searchStudentCourse("3");

    StudentCourse expected1 = new StudentCourse(
        3L, "Art",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 8, 30, 18, 0)
    );

    StudentCourse expected2 = new StudentCourse(
        3L, "AWS",
        LocalDateTime.of(2025, 7, 1, 10, 0),
        LocalDateTime.of(2025, 9, 30, 18, 0)
    );

    assertThat(actual).contains(expected1, expected2);
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
    List<StudentCourse> courses = sut.searchStudentCourse("1");
    StudentCourse course = courses.get(0);
    course.setCourseName("更新Java");
    sut.updateStudentCourse(course);

    List<StudentCourse> actual = sut.searchStudentCourse("1");

    StudentCourse expected = new StudentCourse(
        1L,
        "更新Java",
        LocalDateTime.of(2025, 4, 1, 10, 0),
        LocalDateTime.of(2025, 6, 30, 18, 0)
    );
    // IDは比較に含めないようequals()を定義していればOK
    assertThat(actual).contains(expected);
  }

  // ----------- Delete -----------

  @Test
  void 生徒を論理削除できることのテスト() {
    Student student = sut.findStudentById("1");
    sut.logicalDeleteStudent(student);

    List<Student> students = sut.searchStudent();
    assertThat(students.size()).isEqualTo(4);

    Student deletedStudent = sut.findStudentById("1");
    assertThat(deletedStudent).isEqualTo(new Student(
        1L,
        "Cabn",
        "Carbn",
        "C",
        "cabn@example",
        "everywhere",
        20,
        "male",
        "cabnnoremark",
        true  // ← isDeleted が true に変化している
    ));
  }
}