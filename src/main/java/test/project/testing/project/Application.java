package test.project.testing.project;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

  // 生徒データを保存する Map
  private final Map<String, Integer> studentMap = new HashMap<>();

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  // すべての生徒情報を取得する
  @GetMapping("/students")
  public Map<String, Integer> getStudents() {
    return studentMap;
  }

  // 生徒を追加する
  @PostMapping("/addStudent")
  public String addStudent(@RequestParam String name, @RequestParam Integer age) {
    studentMap.put(name, age);
    return "生徒 " + name + " (年齢: " + age + ") を追加しました。";

    //curl "http://localhost:8080/students"で全生徒確認
    //curl -X POST -d "name=daddy&age=25" "http://localhost:8080/addStudent"で生徒追加
  }
}
