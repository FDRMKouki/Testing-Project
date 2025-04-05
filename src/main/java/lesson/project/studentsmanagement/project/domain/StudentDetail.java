package lesson.project.studentsmanagement.project.domain;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDetail {

  //1つのStudent,複数のStudentCourse
  //1生徒が複数のコース通っていた時を配慮
  private Student student;
  private List<StudentsCourses> studentsCourses;

}
