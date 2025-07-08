package lesson.project.studentsmanagement.project.repository;

import static org.assertj.core.api.Java6Assertions.assertThat;

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
    sut.registerStudent(student);
    //リポとうろくはvoidで返しがない=継承できない ので増加後のリストを取得させる作戦 論理削除されてない受講生を読み取ることに注意
    List<Student> actual = sut.searchStudent();
    //5+1=6
    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void コースが登録できることのテスト() {
    StudentCourse course = new StudentCourse();
    course.setCourseName("Javava");
    sut.registerStudentCourse(course);
    //受講生と同じく増加後のリストを取得させる
    List<StudentCourse> actual = sut.searchStudentCourseList();
    //7+1=8
    assertThat(actual.size()).isEqualTo(8);
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
    Student student = sut.findStudentById("2");
    assertThat(student).isNotNull();
    assertThat(student.getName()).isEqualTo("Slco");
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