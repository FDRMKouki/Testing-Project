package lesson.project.studentsmanagement.project.controller.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentConverterTest {

  private StudentConverter converter;

  @BeforeEach
  void setUp() {
    converter = new StudentConverter();
  }

  @Test
  void 論理削除されていない生徒のみが変換されることのテスト() {
    // Arrange
    Student activeStudent = new Student(1L, null, null, null, null, null, 0, null, null, false);
    Student deletedStudent = new Student(2L, null, null, null, null, null, 0, null, null, true);

    List<Student> studentList = List.of(activeStudent, deletedStudent);
    List<StudentCourse> courseList = List.of(); // courseは今回不要

    // Act
    List<StudentDetail> result = converter.convertStudentDetails(studentList, courseList);

    // Assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStudent().getId()).isEqualTo(1L);
  }

  @Test
  void 生徒に紐づくコースが正しく変換されていることのテスト() {
    // Arrange
    Student student = new Student(1L, null, null, null, null, null, 0, null, null, false);
    StudentCourse course1 = new StudentCourse(1L, "Java", null, null);
    StudentCourse course2 = new StudentCourse(1L, "Spring", null, null);
    StudentCourse unrelatedCourse = new StudentCourse(2L, "Java", null, null);// 別人のコース

    List<Student> studentList = List.of(student);
    List<StudentCourse> courseList = List.of(course1, course2, unrelatedCourse);

    // Act
    List<StudentDetail> result = converter.convertStudentDetails(studentList, courseList);

    // Assert
    assertThat(result).hasSize(1);
    List<StudentCourse> convertedCourses = result.get(0).getStudentCourseList();
    assertThat(convertedCourses).hasSize(2);
    assertThat(convertedCourses)//変換後にコース名などは変わってはいけない
        .extracting("courseName")
        .containsExactlyInAnyOrder("Java", "Spring");
  }

  @Test
  void コースが存在しない場合でも空リストが設定されることのテスト() {
    // Arrange
    Student student = new Student(1L, null, null, null, null, null, 0, null, null, false);

    List<Student> studentList = List.of(student);
    List<StudentCourse> emptyCourseList = List.of(); // コースなし

    // Act
    List<StudentDetail> result = converter.convertStudentDetails(studentList, emptyCourseList);

    // Assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStudentCourseList()).isEmpty();
  }

}