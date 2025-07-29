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

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Schema(description = "受講生情報")
@Getter
@Setter

public class Student {

  @EqualsAndHashCode.Include
  @NotNull(message = "IDは必須です", groups = UpdateGroup.class)
  //@NotBlank使用禁止！！！
  private Long id; // ← String → Long に変更

  @EqualsAndHashCode.Include
  @NotBlank(message = "名前は空白にできません", groups = UpdateGroup.class)
  private String name;

  @EqualsAndHashCode.Include
  @NotBlank(message = "フリガナは空白にできません", groups = UpdateGroup.class)
  private String furigana;

  @EqualsAndHashCode.Include
  @NotBlank(message = "ニックネームは空白にできません", groups = UpdateGroup.class)
  private String nickname;

  @EqualsAndHashCode.Include
  @NotBlank(message = "メールアドレスは空白にできません", groups = UpdateGroup.class)
  @Pattern(
      regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
      message = "メールアドレスの形式が正しくありません",
      groups = UpdateGroup.class
  )
  //@Email(message = "メールアドレスの形式が正しくありません") <- @Emailだとtest@example.comなどの形じゃないと通らないらしい
  private String mailAddress;

  @EqualsAndHashCode.Include
  @NotBlank(message = "都道府県は空白にできません", groups = UpdateGroup.class)
  private String region;

  @EqualsAndHashCode.Include
  private int age;

  @EqualsAndHashCode.Include
  @NotBlank(message = "性別は空白にできません", groups = UpdateGroup.class)
  private String gender;

  @EqualsAndHashCode.Include
  private String remark;

  @EqualsAndHashCode.Include
  private boolean deleted;

  @Schema(hidden = true)
  private List<StudentCourse> studentCourse;
//Postmanにて、送るjsonはこれに合わせる必要がある
  
  public Student() {
    // デフォルトコンストラクタ（Spring MVCのバインディング用）
  }

  public Student(Long id, String name, String furigana, String nickname,
      String mailAddress, String region, int age,
      String gender, String remark, boolean deleted) {
    this.id = id;
    this.name = name;
    this.furigana = furigana;
    this.nickname = nickname;
    this.mailAddress = mailAddress;
    this.region = region;
    this.age = age;
    this.gender = gender;
    this.remark = remark;
    this.deleted = deleted;
  }


}
