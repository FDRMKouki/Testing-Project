package test.project.testing.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

  //curl -X POST -d "name=konchiwawa&age=1000" "http://localhost:8080/studentInfo"
  //↓
  //curl "http://localhost:8080/studentInfo" でkonchiwawa, 1000歳と表示
  private String name = "kouki";
  private String age = "100";

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @GetMapping("/studentInfo")
  public String getStudentInfo() {
    return name + ", " + age + "歳";
  }

  @PostMapping("/studentInfo")
  public void setStudentInfo(String name, String age) {
    this.name = name;
    this.age = age;
  }

  @PostMapping("/studentName")
  public void setStudentName(String name) {
    this.name = name;
  }
}
