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
  void 生徒に紐づくコース申込状況が正しく変換されることのテスト() {
    // Arrange
    Student student = new Student(1L, null, null, null, null, null, 0, null, null, false);
    StudentCourse course1 = new StudentCourse(101L, 1L, "Java", null, null);
    StudentCourse course2 = new StudentCourse(102L, 1L, "Spring", null, null);
    CourseStatus status1 = new CourseStatus(101L, 1);
    CourseStatus status2 = new CourseStatus(102L, 2);
    CourseStatus unrelatedStatus = new CourseStatus(999L, 3);

    List<Student> studentList = List.of(student);
    List<StudentCourse> courseList = List.of(course1, course2);
    List<CourseStatus> statusList = List.of(status1, status2, unrelatedStatus);

    // Act
    List<StudentDetail> result = converter.convertStudentDetails(studentList, courseList,
        statusList);

    // Assert
    assertThat(result).hasSize(1);
    List<CourseStatus> convertedStatuses = result.get(0).getCourseStatusList();
    assertThat(convertedStatuses).extracting("courseId")
        .containsExactlyInAnyOrder(101L, 102L);
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

  @Test
  void 複数生徒に対して正しく変換されることのテスト() {
    Student student1 = new Student(1L, null, null, null, null, null, 0, null, null, false);
    Student student2 = new Student(2L, null, null, null, null, null, 0, null, null, false);

    StudentCourse course1 = new StudentCourse(101L, 1L, "Java", null, null);
    StudentCourse course2 = new StudentCourse(102L, 2L, "Spring", null, null);

    CourseStatus status1 = new CourseStatus(101L, 1);
    CourseStatus status2 = new CourseStatus(102L, 2);

    List<Student> studentList = List.of(student1, student2);
    List<StudentCourse> courseList = List.of(course1, course2);
    List<CourseStatus> statusList = List.of(status1, status2);

    List<StudentDetail> result = converter.convertStudentDetails(studentList, courseList,
        statusList);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getStudentCourseList()).extracting("courseName")
        .containsExactly("Java");
    assertThat(result.get(1).getStudentCourseList()).extracting("courseName")
        .containsExactly("Spring");
  }

  @Test
  void 全員削除済みなら空リストが返ることのテスト() {
    Student deleted1 = new Student(1L, null, null, null, null, null, 0, null, null, true);
    Student deleted2 = new Student(2L, null, null, null, null, null, 0, null, null, true);

    List<Student> studentList = List.of(deleted1, deleted2);
    List<StudentCourse> courseList = List.of();
    List<CourseStatus> statusList = List.of();

    List<StudentDetail> result = converter.convertStudentDetails(studentList, courseList,
        statusList);

    assertThat(result).isEmpty();
  }

  @Test
  void 全て空リストなら空結果が返ることのテスト() {
    List<StudentDetail> result = converter.convertStudentDetails(List.of(), List.of(), List.of());
    assertThat(result).isEmpty();
  }


}