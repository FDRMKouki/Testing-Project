package lesson.project.studentsmanagement.project;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import lesson.project.studentsmanagement.project.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

  @Autowired
  private StudentRepository repository;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  //Read 生徒情報を取得するcurl "http://localhost:8080/studentList"
  @GetMapping("/studentList")
  public List<Student> getStudentList() {
    return repository.searchStudent();
  }

  //Read 生徒コース情報を取得するcurl "http://localhost:8080/studentCourseList"
  @GetMapping("/studentCourseList")
  public List<StudentCourse> getStudentCourseList() {
    return repository.searchStudentCourse();
  }
}
