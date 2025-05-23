package lesson.project.studentsmanagement.project.data;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

  private Long id; // ← String → Long に変更
  private String name;
  private String furigana;
  private String nickname;
  private String mailAddress;
  private String region;
  private int age;
  private String gender;
  private String remark;
  private boolean isDeleted;
  private List<StudentCourse> studentCourse;
//Postmanにて、送るjsonはこれに合わせる必要がある
}
