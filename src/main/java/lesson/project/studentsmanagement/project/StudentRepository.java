package lesson.project.studentsmanagement.project;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StudentRepository {

  @Select("SELECT * FROM student WHERE name=#{name}")
  Student searchByName(String name);

  @Select("SELECT * FROM student")
  List<Student> searchAll();

  @Insert("INSERT student values(#{name},#{age})")
  void registerStudent(@Param("name") String name, @Param("age") int age);

  @Update("UPDATE student SET age=#{age} WHERE name=#{name}")
  void updateStudent(String name, int age);

  @Delete("DELETE FROM student WHERE name=#{name}")
  void deleteStudent(String name);
}
