package lesson.project.studentsmanagement.project.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lesson.project.studentsmanagement.project.validation.UpdateGroup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Schema(description = "受講生情報")
@Getter
@Setter
public class Student {


  @NotNull(message = "IDは必須です", groups = UpdateGroup.class)
  //@NotBlank使用禁止！！！
  private Long id; // ← String → Long に変更

  @NotBlank(message = "名前は空白にできません", groups = UpdateGroup.class)
  private String name;

  @NotBlank(message = "フリガナは空白にできません", groups = UpdateGroup.class)
  private String furigana;

  @NotBlank(message = "ニックネームは空白にできません", groups = UpdateGroup.class)
  private String nickname;

  @NotBlank(message = "メールアドレスは空白にできません", groups = UpdateGroup.class)
  @Pattern(
      regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
      message = "メールアドレスの形式が正しくありません",
      groups = UpdateGroup.class
  )
  //@Email(message = "メールアドレスの形式が正しくありません") <- @Emailだとtest@example.comなどの形じゃないと通らないらしい
  private String mailAddress;

  @NotBlank(message = "都道府県は空白にできません", groups = UpdateGroup.class)
  private String region;

  private int age;

  @NotBlank(message = "性別は空白にできません", groups = UpdateGroup.class)
  private String gender;


  private String remark;

  private boolean deleted;

  @Schema(hidden = true)
  private List<StudentCourse> studentCourse;
//Postmanにて、送るjsonはこれに合わせる必要がある
}
