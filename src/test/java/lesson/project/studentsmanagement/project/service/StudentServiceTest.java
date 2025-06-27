package lesson.project.studentsmanagement.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lesson.project.studentsmanagement.project.controller.converter.StudentConverter;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import lesson.project.studentsmanagement.project.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;
  @Mock
  private StudentConverter converter;

  private StudentService sut;

  @BeforeEach
  void beforePrepare() {
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生詳細一覧検索_リポジトリとコンバーターの処理が適切に呼び出されていることのテスト() {
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    Mockito.when(repository.searchStudent()).thenReturn(studentList);
    Mockito.when(repository.searchStudentCourseList()).thenReturn(studentCourseList);

    sut.searchStudentList();

    verify(repository, times(1)).searchStudent();
    verify(repository, times(1)).searchStudentCourseList();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);
  }

  @Test
  void 受講生詳細検索_リポジトリの処理が適切に呼び出されていることのテスト() {
    String id = "123";
    Student student = new Student();
    student.setId(123L);
    student.setName("検索太郎");

    StudentCourse course = new StudentCourse();
    course.setCourseName("Java");

    List<StudentCourse> courseList = List.of(course);

    Mockito.when(repository.findStudentById(id)).thenReturn(student);
    Mockito.when(repository.searchStudentCourse(id)).thenReturn(courseList);

    StudentDetail result = sut.getStudentDetailById(id);

    verify(repository, times(1)).findStudentById(id);
    verify(repository, times(1)).searchStudentCourse(id);

    assertThat(result.getStudent()).isEqualTo(student);
    assertThat(result.getStudentCourseList()).isEqualTo(courseList);
  }

  @Test
  void 受講生登録_リポジトリの処理が適切に呼び出されていることのテスト() {
    Student student = new Student();
    student.setId(100L);
    student.setName("登録太郎");

    StudentCourse course = new StudentCourse();
    course.setCourseName("Java");

    List<StudentCourse> courseList = List.of(course);
    StudentDetail detail = new StudentDetail(student, courseList);

    StudentDetail result = sut.registerStudent(detail);

    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(1)).registerStudentCourse(Mockito.any(StudentCourse.class));

    assertThat(result).isEqualTo(detail);
    assertThat(course.getStartDatetimeAt().getHour()).isEqualTo(LocalDateTime.now().getHour());
    assertThat(course.getPredictedCompleteDatetimeAt().getYear())
        .isEqualTo(LocalDateTime.now().plusYears(1).getYear());
  }

  @Test
  void 受講生更新_リポジトリの処理が適切に呼び出されていることのテスト() {
    Student student = new Student();
    student.setId(200L);
    student.setName("変更太郎");

    StudentCourse course = new StudentCourse();
    course.setCourseName("変更後Java");

    StudentDetail detail = new StudentDetail(student, List.of(course));

    sut.updateStudent(detail);

    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(1)).updateStudentCourse(course);
  }

  @Test
  void 生徒IDがnullのときIllegalArgumentExceptionがスローされることのテスト() {
    Student student = new Student();  // ID未設定(ID=null)
    StudentDetail detail = new StudentDetail(student, List.of());

    assertThrows(IllegalArgumentException.class, () -> sut.updateStudent(detail));
  }
}
