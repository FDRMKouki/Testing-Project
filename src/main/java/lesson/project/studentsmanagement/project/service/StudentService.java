package lesson.project.studentsmanagement.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lesson.project.studentsmanagement.project.controller.converter.StudentConverter;
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

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime oneYearLater = now.plusYears(1);

    for (StudentCourse course : courses) {
      initStudentsCourse(course, student, now, oneYearLater);
      repository.registerStudentCourse(course);
    }

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
    return converter.convertStudentDetails(studentList, studentCoursesList);
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
    List<StudentCourse> courses = repository.searchStudentCourse(id);
    return new StudentDetail(student, courses);
  }

  // ----------- Update -----------

  /**
   * 生徒とコース名を更新する。 生徒とコースの情報をそれぞれ更新
   *
   * @param studentDetail 更新内容
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    if (studentDetail.getStudent() == null || studentDetail.getStudent().getId() == null) {
      throw new IllegalArgumentException("IDは必須です。");
    }

    repository.updateStudent(studentDetail.getStudent());
    studentDetail.getStudentCourseList()
        .forEach(studentCourse -> repository.updateStudentCourse(studentCourse));
  }

  // ----------- Delete -----------

  /**
   * 論理削除（is_deleted = true など）を実施。
   *
   * @param student 対象の生徒
   */
  public void logicalDeleteStudent(Student student) {
    repository.logicalDeleteStudent(student);
  }
}
