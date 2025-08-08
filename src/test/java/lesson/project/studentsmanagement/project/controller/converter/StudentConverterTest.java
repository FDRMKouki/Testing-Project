package lesson.project.studentsmanagement.project.controller.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lesson.project.studentsmanagement.project.data.CourseStatus;
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
    List<CourseStatus> statusList = List.of();

    // Act
    List<StudentDetail> result = converter.convertStudentDetails(studentList, courseList,
        statusList);

    // Assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStudent().getId()).isEqualTo(1L);
  }

  @Test
  void 生徒に紐づくコースが正しく変換されていることのテスト() {
    // Arrange
    Student student = new Student(1L, null, null, null, null, null, 0, null, null, false);
    StudentCourse course1 = new StudentCourse(1L, 1L, "Java", null, null);
    StudentCourse course2 = new StudentCourse(2L, 1L, "Spring", null, null);
    StudentCourse unrelatedCourse = new StudentCourse(3L, 2L, "Java", null, null);// 別人のコース
    CourseStatus status1 = new CourseStatus(1L, 1);
    CourseStatus status2 = new CourseStatus(2L, 1);
    CourseStatus unrelatedStatus = new CourseStatus(3L, 1);

    List<Student> studentList = List.of(student);
    List<StudentCourse> courseList = List.of(course1, course2, unrelatedCourse);
    List<CourseStatus> statusList = List.of(status1, status2, unrelatedStatus);

    // Act
    List<StudentDetail> result = converter.convertStudentDetails(studentList, courseList,
        statusList);

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
    List<CourseStatus> emptyStatusList = List.of(); //   情報なし

    // Act
    List<StudentDetail> result = converter.convertStudentDetails(studentList, emptyCourseList,
        emptyStatusList);

    // Assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStudentCourseList()).isEmpty();
  }

}