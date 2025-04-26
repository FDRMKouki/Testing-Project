package lesson.project.studentsmanagement.project.data;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsCourses {

  private Long id; // ← String → Long に変更
  private Long studentId; // ← String → Long に変更
  private String courseName;
  private LocalDateTime startDatetimeAt;
  private LocalDateTime predictedCompleteDatetimeAt;

}
