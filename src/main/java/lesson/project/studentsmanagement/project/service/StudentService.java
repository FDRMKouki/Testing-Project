package lesson.project.studentsmanagement.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lesson.project.studentsmanagement.project.controller.converter.StudentConverter;
import lesson.project.studentsmanagement.project.data.CourseStatus;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import lesson.project.studentsmanagement.project.exception.StudentNotFoundException;
import lesson.project.studentsmanagement.project.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 受講生情報の登録、検索、更新、削除を行うサービス。
 */
@Service
public class StudentService {

  private final StudentRepository repository;
  private final StudentConverter converter;

  @Autowired
  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  // ----------- Create -----------

  /**
   * 生徒を登録する。 IDは自動採番、IDに紐づくコース情報は終了予定日程が現在日程の1年後になってる
   *
   * @param studentDetail 登録する生徒情報
   * @return 登録された生徒詳細
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();
    repository.registerStudent(student);  // IDがセットされる

    List<StudentCourse> courses = filterValidCourses(studentDetail.getStudentCourseList());
    validateCourses(courses);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime oneYearLater = now.plusYears(1);

    for (StudentCourse course : courses) {
      initStudentsCourse(course, student, now, oneYearLater);
      repository.registerStudentCourse(course); // ここでIDがセットされる
    }

    // ここで courseStatus 登録
    for (StudentCourse course : courses) {  // 元は studentDetail.getStudentCourseList() だが、ID反映のため coursesを使う
      CourseStatus status = new CourseStatus(course.getId(), 1);
      repository.registerCourseStatus(status);
    }

    // ここで ID がセットされた courses を studentDetail に反映する
    studentDetail.setStudentCourseList(courses);

    return studentDetail;
  }

  /**
   * コース情報を登録するときの初期情報の設定
   *
   * @param course       コース情報
   * @param student      生徒
   * @param now          今
   * @param oneYearLater 今から1年後
   */
  private void initStudentsCourse(StudentCourse course, Student student, LocalDateTime now,
      LocalDateTime oneYearLater) {
    course.setStudentId(student.getId());
    course.setStartDatetimeAt(now);
    course.setPredictedCompleteDatetimeAt(oneYearLater);
  }

  private List<StudentCourse> filterValidCourses(List<StudentCourse> courses) {
    if (courses == null) {
      return List.of();
    }
    return courses.stream()
        .filter(c -> c.getCourseName() != null && !c.getCourseName().trim().isEmpty())
        .collect(Collectors.toList());
  }

  // ----------- Read -----------

  /**
   * 削除されていない全ての生徒情報を取得。
   *
   * @return 全受講生の詳細情報リスト
   */
  public List<StudentDetail> searchStudentList() {
    List<Student> studentList = repository.searchStudent();
    List<StudentCourse> studentCoursesList = repository.searchStudentCourseList();
    List<CourseStatus> courseStatusList = repository.searchCourseStatusList();
    return converter.convertStudentDetails(studentList, studentCoursesList, courseStatusList);
  }

  /**
   * 指定IDの受講生情報を取得。
   *
   * @param id 生徒ID
   * @return 生徒詳細
   */
  public StudentDetail getStudentDetailById(String id) {
    Student student = repository.findStudentById(id);
    if (student == null) {
      throw new StudentNotFoundException("生徒ID " + id + " は存在しません。");
    }
    List<StudentCourse> courses = repository.findStudentCourseByStudentId(id);

    // コースIDリストを作成
    List<Long> courseIds = courses.stream()
        .map(StudentCourse::getId)
        .collect(Collectors.toList());

    // コースIDに対応する申込状況を一括取得
    List<CourseStatus> courseStatuses = repository.findCourseStatusesByCourseIds(
        courseIds.stream()
            .map(String::valueOf)
            .collect(Collectors.toList())
    );

    // CourseStatusをcourseIdをキーにMap化
    Map<Long, Integer> courseStatusMap = courseStatuses.stream()
        .collect(Collectors.toMap(
            cs -> Long.valueOf(cs.getCourseId()),
            CourseStatus::getAppStatus // Integer型をそのまま使う
        ));

    // StudentCourse に appStatus をセット
    for (StudentCourse course : courses) {
      Integer statusInt = courseStatusMap.get(course.getId());
      course.setAppStatus(statusInt != null ? String.valueOf(statusInt) : "1"); // デフォルト仮申込
    }
    // 空リストを設定（StudentCourseのnull防止）
    if (courses == null) {
      courses = List.of();
    }

    return new StudentDetail(student, courses, courseStatuses);
  }


  /**
   * 条件で生徒を検索（名前・ふりがな・メールアドレスの部分一致）。
   *
   * @param name        名前の一部（null可）
   * @param furigana    フリガナの一部（null可）
   * @param mailAddress メールアドレスの一部（null可）
   * @return 検索結果の生徒詳細リスト
   */
  public List<StudentDetail> searchStudents(String name, String furigana, String mailAddress) {
    List<Student> students = repository.searchStudentsByConditions(name, furigana, mailAddress);

    if (students.isEmpty()) {
      return List.of();
    }

    // IDリストを使ってコースと申込状況を一括取得
    List<String> studentIds = students.stream()
        .map(s -> s.getId().toString())
        .collect(Collectors.toList());

    List<StudentCourse> courses = repository.findStudentCoursesByStudentIds(studentIds);
    List<CourseStatus> statuses = repository.findCourseStatusesByCourseIds(
        courses.stream().map(c -> c.getId().toString()).collect(Collectors.toList())
    );

    // CourseStatus を courseId をキーにMap化
    Map<Long, Integer> courseStatusMap = statuses.stream()
        .collect(Collectors.toMap(
            cs -> Long.valueOf(cs.getCourseId()),
            CourseStatus::getAppStatus
        ));

    // StudentCourse に appStatus をセット
    for (StudentCourse course : courses) {
      Integer statusInt = courseStatusMap.get(course.getId());
      course.setAppStatus(statusInt != null ? String.valueOf(statusInt) : "1");
    }

    return converter.convertStudentDetails(students, courses, statuses);
  }

  // ----------- Update -----------

  /**
   * 生徒とコース名を更新する。 生徒とコースの情報と申込状況をそれぞれ更新
   *
   * @param studentDetail 更新内容
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    if (studentDetail.getStudent() == null || studentDetail.getStudent().getId() == null) {
      throw new IllegalArgumentException("IDは必須です。");
    }

    List<StudentCourse> courses = studentDetail.getStudentCourseList();
    validateCourses(courses);
    repository.updateStudent(studentDetail.getStudent());

    studentDetail.getStudentCourseList()
        .forEach(studentCourse -> {
          repository.updateStudentCourse(studentCourse);

          if (studentCourse.getId() != null && studentCourse.getAppStatus() != null) {
            int statusInt = Integer.parseInt(studentCourse.getAppStatus());
            repository.updateCourseStatus(studentCourse.getId().toString(), statusInt);
          }
        });
  }

  // ----------- Delete -----------

  /**
   * 論理削除（is_deleted = true など）を実施。
   *
   * @param student 対象の生徒
   */
  public void logicalDeleteStudent(Student student) {
    repository.logicalDeleteStudent(student.getId());
  }


  private void validateCourses(List<StudentCourse> courses) {
    if (courses == null || courses.isEmpty()) {
      throw new IllegalArgumentException("少なくとも1つのコース情報が必要です。");
    }

    for (StudentCourse course : courses) {
      if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
        throw new IllegalArgumentException("コース名は空にできません。");
      }
    }
  }

}
