package lesson.project.studentsmanagement.project.service;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
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

  //生徒リスト取得リポ呼び出し
  public List<Student> searchStudentList() {
    repository.searchStudent();
    return repository.searchStudent();
  }

  //生徒コースリスト取得リポ呼び出し
  public List<StudentsCourses> searchStudentsCourseList() {
    return repository.searchStudentCourse();
  }

  @Transactional
  //生徒登録リポ呼び出し
  public void registerStudent(Student student) {
    repository.registerStudent(student);

    //TODO:第28回の映像をよく見て、受講生コース情報も登録できるようにする。
  }

}