package lesson.project.studentsmanagement.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lesson.project.studentsmanagement.project.controller.converter.StudentConverter;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
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
   * 生徒を登録する。
   *
   * @param studentDetail 登録する生徒情報
   * @return 登録された生徒詳細
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();
    repository.registerStudent(student);  // IDがセットされる

    List<StudentsCourses> courses = filterValidCourses(studentDetail.getStudentsCourses());

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime oneYearLater = now.plusYears(1);

    for (StudentsCourses course : courses) {
      course.setStudentId(student.getId());
      course.setStartDatetimeAt(now);
      course.setPredictedCompleteDatetimeAt(oneYearLater);
      repository.registerStudentsCourses(course);
    }

    return studentDetail;
  }

  private List<StudentsCourses> filterValidCourses(List<StudentsCourses> courses) {
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
    List<StudentsCourses> studentCoursesList = repository.searchStudentCourse();
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
    List<StudentsCourses> courses = repository.findCoursesByStudentId(id);
    return new StudentDetail(student, courses);
  }

  // ----------- Update -----------

  /**
   * 生徒とコース名を更新する。
   *
   * @param studentDetail 更新内容
   */
  public void updateStudent(StudentDetail studentDetail) {
    repository.updateStudent(studentDetail.getStudent());
    for (StudentsCourses course : studentDetail.getStudentsCourses()) {
      repository.updateStudentsCourses(course);
    }
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
