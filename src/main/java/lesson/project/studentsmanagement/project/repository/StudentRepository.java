package lesson.project.studentsmanagement.project.repository;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 受講生情報を扱うリポ。 全件検索や条件付き検索などができるようにする
 */
@Mapper
public interface StudentRepository {

  /**
   * 全件検索する
   *
   * @return　全件検索結果
   */

  @Select("SELECT * FROM students")
  List<Student> searchStudent();

  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchStudentCourse();

}
