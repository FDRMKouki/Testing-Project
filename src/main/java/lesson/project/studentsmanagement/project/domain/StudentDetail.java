package lesson.project.studentsmanagement.project.domain;

import java.util.ArrayList;
import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDetail {

  private Student student = new Student();

  private List<StudentsCourses> studentsCourses = new ArrayList<>();

  public StudentDetail() {
    // 初期状態で1つのコースを追加
    studentsCourses.add(new StudentsCourses());
  }

  // getter/setter
}