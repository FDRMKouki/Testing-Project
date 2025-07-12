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
    Student student = new Student();
    student.setName("登録太郎");
    student.setFurigana("トウロクタロウ");
    student.setNickname("とろたろ");
    student.setMailAddress("taro@example.com");
    student.setRegion("関東");
    student.setAge(25);
    student.setGender("male");
    student.setRemark("テスト登録");
    student.setDeleted(false);
    sut.registerStudent(student);
    //リポとうろくはvoidで返しがない=継承できない ので増加後のリストを取得させる作戦 論理削除されてない受講生を読み取ることに注意
    List<Student> actual = sut.searchStudent();
    //5+1=6
    assertThat(actual.size()).isEqualTo(6);
    Student inserted = actual.stream()
        .filter(s -> "登録太郎".equals(s.getName()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("登録した生徒が見つかりません"));

    // 登録した内容の検証
    assertThat(inserted.getFurigana()).isEqualTo("トウロクタロウ");
    assertThat(inserted.getNickname()).isEqualTo("とろたろ");
    assertThat(inserted.getMailAddress()).isEqualTo("taro@example.com");
    assertThat(inserted.getRegion()).isEqualTo("関東");
    assertThat(inserted.getAge()).isEqualTo(25);
    assertThat(inserted.getGender()).isEqualTo("male");
    assertThat(inserted.getRemark()).isEqualTo("テスト登録");
  }

  @Test
  void コースが登録できることのテスト() {
    StudentCourse course = new StudentCourse();
    course.setStudentId(2L); //結びつく生徒IDであって、コースのIDではない
    course.setCourseName("Javava");
    course.setStartDatetimeAt(LocalDateTime.of(2025, 7, 1, 10, 0));
    course.setPredictedCompleteDatetimeAt(LocalDateTime.of(2025, 9, 30, 18, 0));
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

    assertThat(inserted.getStartDatetimeAt()).isEqualTo(LocalDateTime.of(2025, 7, 1, 10, 0));
    assertThat(inserted.getPredictedCompleteDatetimeAt()).isEqualTo(
        LocalDateTime.of(2025, 9, 30, 18, 0));
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
    Student expected = new Student();
    expected.setId(2L);
    expected.setName("Slco");
    expected.setFurigana("Silico");
    expected.setNickname("Si");
    expected.setMailAddress("slco@example");
    expected.setRegion("here");
    expected.setAge(20);
    expected.setGender("female");
    expected.setRemark("slconoremark");
    expected.setDeleted(false);

    // 実際に取得
    Student actual = sut.findStudentById("2");

    //全フィールドがっちり一致を目指す
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void 生徒IDに紐づくコース情報が取得できることのテスト() {
    //Pは2つもコースがある
    List<StudentCourse> courses = sut.searchStudentCourse("3");
    assertThat(courses.size()).isEqualTo(2);
    assertThat(courses.get(0).getCourseName()).isEqualTo("Art");
    assertThat(courses.get(1).getCourseName()).isEqualTo("AWS");
  }

  // ----------- Update -----------

  @Test
  void 生徒情報が更新できることのテスト() {
    Student student = sut.findStudentById("1");
    student.setName("ダイヤ更新済み");
    sut.updateStudent(student);

    Student updated = sut.findStudentById("1");
    assertThat(updated.getName()).isEqualTo("ダイヤ更新済み");
  }

  @Test
  void コース情報が更新できることのテスト() {
    List<StudentCourse> courses = sut.searchStudentCourse("1");
    StudentCourse course = courses.get(0);
    course.setCourseName("更新Java");
    sut.updateStudentCourse(course);

    List<StudentCourse> updatedCourses = sut.searchStudentCourse("1");
    assertThat(updatedCourses.get(0).getCourseName()).isEqualTo("更新Java");
  }

  // ----------- Delete -----------

  @Test
  void 生徒を論理削除できることのテスト() {
    Student student = sut.findStudentById("1");
    sut.logicalDeleteStudent(student);

    List<Student> students = sut.searchStudent();
    // ID=1が論理削除されたため4人になるはず
    assertThat(students.size()).isEqualTo(4);
    // 確認としても、削除された生徒の is_deleted フラグも見てみる
    Student deletedStudent = sut.findStudentById("1");
    assertThat(deletedStudent).isNotNull();
  }
}