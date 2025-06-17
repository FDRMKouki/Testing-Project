package lesson.project.studentsmanagement.project.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生コース情報")
@Getter
@Setter
public class StudentCourse {

  private Long id; // ← String → Long に変更
  private Long studentId; // ← String → Long に変更
  private String courseName;
  private LocalDateTime startDatetimeAt;
  private LocalDateTime predictedCompleteDatetimeAt;

}
