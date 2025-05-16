package lesson.project.studentsmanagement.project.service;

import java.time.LocalDateTime;
import java.util.List;
import lesson.project.studentsmanagement.project.controller.converter.StudentConverter;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import lesson.project.studentsmanagement.project.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 受講生情報を取り扱うサービス。 検索、登録、更新などを行う。
 */
@Service
public class StudentService {

  private StudentRepository repository;
  private StudentConverter converter;

  @Autowired
  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  @Transactional
  //生徒の登録 CREATE
//---------------

/**
 *登録処理
 */
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    // 1. 生徒情報を登録して、自動採番されたIDを取得
    Student student = studentDetail.getStudent();
    repository.registerStudent(student); // ここで student.id がセットされる

    // 2. コース情報を登録（リストが null でない場合）
    List<StudentsCourses> courses = studentDetail.getStudentsCourses();
    if (courses != null) {
      //名前が空のコースは登録されない
      courses = courses.stream()
          .filter(c -> c.getCourseName() != null && !c.getCourseName().trim().isEmpty())
          .toList();
      //開始日程は現在、終了予定日程は現在の1年後に
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime oneYearLater = now.plusYears(1);

      for (StudentsCourses course : courses) {
        course.setStudentId(student.getId()); // 自動採番IDをセット！
        course.setStartDatetimeAt(now);
        course.setPredictedCompleteDatetimeAt(oneYearLater);//この行2つは日程の登録
        repository.registerStudentsCourses(course);
      }
    }
    return studentDetail;
  }

  //生徒の表示系 READ
//---------------

  /**
   * 削除されていない全ての生徒情報を取得する全件検索。
   *
   * @return 受講生(全件)
   */
  //生徒リスト取得リポ呼び出し
  public List<StudentDetail> searchStudentList() {
    List<Student> studentList = repository.searchStudent();
    List<StudentsCourses> studentCoursesList = repository.searchStudentCourse();
    return converter.convertStudentDetails(studentList, studentCoursesList);
  }

  /**
   * 単一の生徒情報を取得する。コース情報はその受講生のIDに紐づくものを持ってくるようにする
   *
   * @param id 受講生ID
   * @return そのIDの受講生の詳細
   */
  //名前をクリックされた生徒情報取得リポ呼び出し
  public StudentDetail getStudentDetailById(String id) {
    Student student = repository.findStudentById(id);
    List<StudentsCourses> courses = repository.findCoursesByStudentId(id);
    return new StudentDetail(student, courses);
  }

  //生徒の更新 UPDATE
//---------------

  /**
   * 生徒の更新
   *
   * @param studentDetail 受講生詳細
   */
  public void updateStudent(StudentDetail studentDetail) {
    //初めに生徒詳細の更新
    repository.updateStudent(studentDetail.getStudent());
    //コースの名前更新
    for (StudentsCourses course : studentDetail.getStudentsCourses()) {
      repository.updateStudentsCourses(course); // id指定で更新
    }
  }

  //生徒の削除 DELETE
//---------------

  /**
   * 論理削除処理 DELETE
   *
   * @param student
   */
  public void logicalDeleteStudent(Student student) {
    repository.logicalDeleteStudent(student);
  }
}