package lesson.project.studentsmanagement.project.service;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import lesson.project.studentsmanagement.project.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  //生徒リスト取得リポ呼び出し
  public List<Student> searchStudentList() {
    repository.searchStudent();
    return repository.searchStudent();
  }

  //生徒コースリスト取得リポ呼び出し
  public List<StudentsCourses> searchStudentsCourseList() {
    return repository.searchStudentCourse();
  }

  //生徒登録リポ呼び出し
  public void registerStudent(Student student) {
    repository.registerStudent(student);
  }

//  // 仮のコース情報をDBに登録するメソッド
//  public void insertDefaultCourseForStudent(String studentId) {
//    // 仮のコース情報を作成
//    StudentsCourses studentsCourses = new StudentsCourses();
//    studentsCourses.setStudentId(studentId);
//    studentsCourses.setCourseName("仮コース");
//    studentsCourses.setStartDatetimeAt(LocalDate.now());  // 現在の日付を開始日として設定
//    studentsCourses.setPredictedCompleteDatetimeAt(
//        LocalDate.now().plusMonths(1));  // 1ヶ月後を予測完了日として設定
//
//    // コース情報をDBに登録
//    repository.insertCourse(studentsCourses);
//  }

}