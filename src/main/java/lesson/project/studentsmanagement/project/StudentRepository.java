package lesson.project.studentsmanagement.project;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentRepository {

  @Select("SELECT * FROM students")
  List<Student> searchStudent();

  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchStudentCourse();

  //映像にはないので念のためコメント化
//  @Select("SELECT * FROM student")
//  List<Student> searchAll();

}
