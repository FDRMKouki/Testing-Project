package lesson.project.studentsmanagement.project.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生情報")
@Getter
@Setter
public class Student {

  @NotNull(message = "IDは必須です")
  //@NotBlank使用禁止！！！
  private Long id; // ← String → Long に変更

  @NotBlank(message = "名前は空白にできません")
  private String name;

  @NotBlank(message = "フリガナは空白にできません")
  private String furigana;

  @NotBlank(message = "ニックネームは空白にできません")
  private String nickname;

  @NotBlank(message = "メールアドレスは空白にできません")
  @Email(message = "メールアドレスの形式が正しくありません")
  private String mailAddress;

  @NotBlank(message = "都道府県は空白にできません")
  private String region;

  private int age;

  @NotBlank(message = "性別は空白にできません")
  private String gender;


  private String remark;

  private boolean deleted;

  @Schema(hidden = true)
  private List<StudentCourse> studentCourse;
//Postmanにて、送るjsonはこれに合わせる必要がある
}
