package lesson.project.studentsmanagement.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lesson.project.studentsmanagement.project.controller.converter.StudentConverter;
import lesson.project.studentsmanagement.project.data.CourseStatus;
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
    List<CourseStatus> courseStatusList = new ArrayList<>();
    Mockito.when(repository.searchStudent()).thenReturn(studentList);
    Mockito.when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    Mockito.when(repository.searchCourseStatusList()).thenReturn(courseStatusList);

    sut.searchStudentList();

    verify(repository, times(1)).searchStudent();
    verify(repository, times(1)).searchStudentCourseList();
    verify(repository, times(1)).searchCourseStatusList();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList,
        courseStatusList);
  }

  @Test
  void 受講生詳細検索_リポジトリの処理が適切に呼び出されていることのテスト() {
    String id = "123"; // 対象の生徒ID

    Student student = new Student(
        123L, "検索太郎", "ケンサクタロウ", "けんたろ",
        "kensaku@example.com", "東京", 22, "男性", "備考", false
    );

    StudentCourse course = new StudentCourse(
        10L, "Java",
        LocalDateTime.of(2025, 7, 1, 10, 0),
        LocalDateTime.of(2025, 9, 1, 18, 0)
    );

    course.setStudentId(123L);
    List<StudentCourse> courseList = List.of(course);

    CourseStatus courseStatus = new CourseStatus(10L, 3); // 受講中(3)
    List<CourseStatus> courseStatusList = List.of(courseStatus);

    // モック定義
    Mockito.when(repository.findStudentById(id)).thenReturn(student);
    Mockito.when(repository.findStudentCourseByStudentId(id)).thenReturn(courseList);
    Mockito.when(repository.findCourseStatusesByCourseIds(List.of("10")))
        .thenReturn(courseStatusList);

    StudentDetail result = sut.getStudentDetailById(id);

    // 検証
    verify(repository, times(1)).findStudentById(id);
    verify(repository, times(1)).findStudentCourseByStudentId(id);
    verify(repository, times(1)).findCourseStatusesByCourseIds(List.of("10"));

    assertThat(result.getStudent()).isEqualTo(student);
    assertThat(result.getStudentCourseList()).isEqualTo(courseList);
    assertThat(result.getStudentCourseList().get(0).getAppStatus()).isEqualTo("3");
  }


  @Test
  void 受講生登録_リポジトリの処理が適切に呼び出されていることのテスト() {
    Student student = new Student(
        100L, "登録太郎", "トウロクタロウ", "とろたろ",
        "taro@example.com", "東京", 25, "男性", "登録用メモ", false
    );

    LocalDateTime now = LocalDateTime.now();

    StudentCourse course = new StudentCourse(
        100L, "Java", now, now.plusYears(1)
    );
    CourseStatus status = new CourseStatus(100L, 1);

    List<StudentCourse> courseList = List.of(course);
    List<CourseStatus> statusList = List.of(status);
    StudentDetail detail = new StudentDetail(student, courseList, statusList);

    StudentDetail result = sut.registerStudent(detail);

    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(1)).registerStudentCourse(Mockito.any(StudentCourse.class));
    verify(repository, times(1)).registerCourseStatus(Mockito.any(CourseStatus.class));

    assertThat(result).isEqualTo(detail);
    assertThat(course.getStartDatetimeAt().getHour()).isEqualTo(LocalDateTime.now().getHour());
    assertThat(course.getPredictedCompleteDatetimeAt().getYear())
        .isEqualTo(LocalDateTime.now().plusYears(1).getYear());
  }

  @Test
  void 受講生更新_リポジトリの処理が適切に呼び出されていることのテスト() {
    Student student = new Student(
        200L, "変更太郎", "ヘンコウタロウ", "へんたろ",
        "change@example.com", "大阪", 30, "男性", "更新メモ", false
    );

    StudentCourse course = new StudentCourse(
        200L, "変更後Java",
        LocalDateTime.of(2025, 8, 1, 9, 0),
        LocalDateTime.of(2025, 10, 31, 18, 0)
    );

    StudentDetail detail = new StudentDetail(student, List.of(course));

    sut.updateStudent(detail);

    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(1)).updateStudentCourse(course);
  }

  @Test
  void 条件検索時_申込状況が正しく取得されていることのテスト() {
    Student student = new Student(
        101L, "条件検索", "ジョウケンケンサク", "じょけん",
        "search@example.com", "関西", 28, "女性", "備考", false
    );

    StudentCourse course = new StudentCourse(
        201L, "Python",
        LocalDateTime.of(2025, 6, 1, 10, 0),
        LocalDateTime.of(2025, 9, 1, 18, 0)
    );
    course.setStudentId(101L);

    CourseStatus status = new CourseStatus(201L, 2); // 本申込(2)

    Mockito.when(repository.searchStudentsByConditions("条件", null, null))
        .thenReturn(List.of(student));
    Mockito.when(repository.findStudentCoursesByStudentIds(List.of("101")))
        .thenReturn(List.of(course));
    Mockito.when(repository.findCourseStatusesByCourseIds(List.of("201")))
        .thenReturn(List.of(status));
    Mockito.when(converter.convertStudentDetails(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenCallRealMethod(); // 省略可（mockしてるなら）

    List<StudentDetail> results = sut.searchStudents("条件", null, null);

    verify(repository).searchStudentsByConditions("条件", null, null);
    verify(repository).findStudentCoursesByStudentIds(List.of("101"));
    verify(repository).findCourseStatusesByCourseIds(List.of("201"));

    assertThat(results).hasSize(1);
    StudentDetail detail = results.get(0);
    assertThat(detail.getStudent().getName()).isEqualTo("条件検索");
    assertThat(detail.getStudentCourseList()).hasSize(1);
    assertThat(detail.getStudentCourseList().get(0).getAppStatus()).isEqualTo("2");
  }

  @Test
  void 生徒IDがnullのときIllegalArgumentExceptionがスローされることのテスト() {
    Student student = new Student(
        null, "名無し", "ナナシ", "ななし",
        "none@example.com", "不明", 0, "不明", "エラー用", false
    ); // ID未設定(ID=null)
    StudentDetail detail = new StudentDetail(student, List.of(), List.of());

    assertThrows(IllegalArgumentException.class, () -> sut.updateStudent(detail));
  }
}
