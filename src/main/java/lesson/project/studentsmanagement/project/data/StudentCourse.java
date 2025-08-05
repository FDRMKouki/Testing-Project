package lesson.project.studentsmanagement.project.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lesson.project.studentsmanagement.project.validation.CreateGroup;
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
  @NotBlank(message = "コース名は必須です", groups = CreateGroup.class)
  private String courseName;

  @EqualsAndHashCode.Include
  private LocalDateTime startDatetimeAt;

  @EqualsAndHashCode.Include
  private LocalDateTime predictedCompleteDatetimeAt;

  //こいつはhtml表示用
  private String appStatus;

  public StudentCourse(Long studentId, String courseName, LocalDateTime startDatetimeAt,
      LocalDateTime predictedCompleteDatetimeAt) {
    this.studentId = studentId;
    this.courseName = courseName;
    this.startDatetimeAt = startDatetimeAt;
    this.predictedCompleteDatetimeAt = predictedCompleteDatetimeAt;
  }


}
