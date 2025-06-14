package lesson.project.studentsmanagement.project;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//http://localhost:8080/v3/api-docsで.json表示
//http://localhost:8080/swagger-ui/index.htmlでUI表示
@OpenAPIDefinition(info = @Info(title = "受講生管理システム"))
@SpringBootApplication
@ComponentScan(basePackages = "lesson.project.studentsmanagement.project")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }


}