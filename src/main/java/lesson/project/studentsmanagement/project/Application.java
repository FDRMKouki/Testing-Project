package lesson.project.studentsmanagement.project;

import java.util.List;
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

  //curl "http://localhost:8080/studentAll"映像にはないので念のため
//  @GetMapping("/studentAll")
//  public String getAllStudent() {
//    List<Student> students = repository.searchAll();
//    if (students.isEmpty()) {
//      return "生徒が一人も見つかりませんでした";
//    }
//
//    return students.stream()
//        .map(student -> student.getName() + " " + student.getAge() + "歳")
//        .collect(Collectors.joining(", "));
//  }
}
