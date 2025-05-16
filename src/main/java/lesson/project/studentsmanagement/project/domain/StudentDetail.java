package lesson.project.studentsmanagement.project.domain;

import java.util.ArrayList;
import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetail {

  private Student student = new Student();
  private List<StudentsCourses> studentsCourses = new ArrayList<>();


}