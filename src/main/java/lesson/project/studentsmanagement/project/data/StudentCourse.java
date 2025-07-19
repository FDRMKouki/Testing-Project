package lesson.project.studentsmanagement.project.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "受講生コース情報")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StudentCourse {

  private Long id; // ← String → Long に変更

  @EqualsAndHashCode.Include
  private Long studentId; // ← String → Long に変更

  @EqualsAndHashCode.Include
  private String courseName;

  @EqualsAndHashCode.Include
  private LocalDateTime startDatetimeAt;

  @EqualsAndHashCode.Include
  private LocalDateTime predictedCompleteDatetimeAt;

  public StudentCourse(Long studentId, String courseName, LocalDateTime startDatetimeAt,
      LocalDateTime predictedCompleteDatetimeAt) {
    this.studentId = studentId;
    this.courseName = courseName;
    this.startDatetimeAt = startDatetimeAt;
    this.predictedCompleteDatetimeAt = predictedCompleteDatetimeAt;
  }


}
