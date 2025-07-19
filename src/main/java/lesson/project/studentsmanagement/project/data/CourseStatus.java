package lesson.project.studentsmanagement.project.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "コースの申込状況")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CourseStatus {

  private Long id;

  @EqualsAndHashCode.Include
  private Long courseId;

  @EqualsAndHashCode.Include
  private int appStatus;

  public CourseStatus(Long courseId, int appStatus) {
    this.courseId = courseId;
    this.appStatus = appStatus;
  }


}
