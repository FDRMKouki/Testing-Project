package lesson.project.studentsmanagement.project.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

  private String id;
  private String name;
  private String furigana;
  private String nickname;
  private String mailAddress;
  private String region;
  private int age;
  private String gender;

}
