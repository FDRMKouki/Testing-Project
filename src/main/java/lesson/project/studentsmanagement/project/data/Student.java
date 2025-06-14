package lesson.project.studentsmanagement.project.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生情報")
@Getter
@Setter
public class Student {

  private Long id; // ← String → Long に変更
  @NotBlank
  private String name;
  @NotBlank
  private String furigana;
  @NotBlank
  private String nickname;
  @NotBlank
  @Email
  private String mailAddress;
  @NotBlank
  private String region;

  private int age;

  @NotBlank
  private String gender;

  private String remark;

  private boolean deleted;

  @Schema(hidden = true)
  private List<StudentCourse> studentCourse;
//Postmanにて、送るjsonはこれに合わせる必要がある
}
