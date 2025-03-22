package lesson.project.studentsmanagement.project;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

  @Autowired
  private StudentRepository repository;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  //Createせいとうろくcurl -X POST -d "name=Shiranaihito&age=1000000" "http://localhost:8080/student"
  @PostMapping("/student")
  public String registerStudent(@RequestParam String name, @RequestParam int age) {
    repository.registerStudent(name, age);
    return name + " " + age + "歳を追加しました";
  }

  //Read 生徒情報を取得するcurl "http://localhost:8080/student?name=Shiranaihito"
  @GetMapping("/student")
  public String getStudent(@RequestParam String name) {
    Student student = repository.searchByName(name);
    return student.getName() + " " + student.getAge() + "歳";
  }

  //Update生徒年齢更新curl -X PATCH -d "name=Shiranaihito&age=100" "http://localhost:8080/student"
  @PatchMapping("/student")
  public void updateStudent(String name, int age) {
    repository.updateStudent(name, age);
  }

  //Delete生徒さくじょcurl -X DELETE -d "name=Shiranaihito" "http://localhost:8080/student"
  @DeleteMapping("/student")
  public void deleteStudent(String name) {
    repository.deleteStudent(name);
  }

  //curl "http://localhost:8080/studentAll"
  @GetMapping("/studentAll")
  public String getAllStudent() {
    List<Student> students = repository.searchAll();
    if (students.isEmpty()) {
      return "生徒が一人も見つかりませんでした";
    }

    return students.stream()
        .map(student -> student.getName() + " " + student.getAge() + "歳")
        .collect(Collectors.joining(", "));
  }
}
