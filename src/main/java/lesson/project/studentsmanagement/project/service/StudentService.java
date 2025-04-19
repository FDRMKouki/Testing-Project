package lesson.project.studentsmanagement.project.service;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import lesson.project.studentsmanagement.project.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  @Transactional
  //----生徒登録----
  //生徒登録リポ呼び出し
  public void registerStudent(StudentDetail studentDetail) {
    // 1. 生徒情報を登録して、自動採番されたIDを取得
    Student student = studentDetail.getStudent();
    repository.registerStudent(student); // ここで student.id がセットされる

    // 2. コース情報を登録（リストが null でない場合）
    List<StudentsCourses> courses = studentDetail.getStudentsCourses();
    if (courses != null) {
      for (StudentsCourses course : courses) {
        course.setStudentId(student.getId()); // 自動採番IDをセット！
        repository.registerStudentsCourses(course);
      }
    }
  }

  //----生徒表示----
  //生徒リスト取得リポ呼び出し
  public List<Student> searchStudentList() {
    repository.searchStudent();
    return repository.searchStudent();
  }

  //生徒コースリスト取得リポ呼び出し
  public List<StudentsCourses> searchStudentsCourseList() {
    return repository.searchStudentCourse();
  }

  //名前をクリックされた生徒情報取得リポ呼び出し
  public StudentDetail getStudentDetailById(String id) {
    Student student = repository.findStudentById(id);
    List<StudentsCourses> courses = repository.findCoursesByStudentId(id);
    StudentDetail detail = new StudentDetail();
    detail.setStudent(student);
    detail.setStudentsCourses(courses);
    return detail;
  }

  //----生徒更新----
  public void updateStudent(StudentDetail studentDetail) {
    repository.updateStudent(studentDetail.getStudent());
  }

}