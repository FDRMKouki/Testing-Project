package lesson.project.studentsmanagement.project.service;

import java.util.List;
import java.util.stream.Collectors;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import lesson.project.studentsmanagement.project.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<Student> searchStudentList() {
    repository.searchStudent();
    return repository.searchStudent().stream()
        .filter(student -> student.getAge() >= 30 && student.getAge() < 40)
        .collect(Collectors.toList());
  }

  public List<StudentCourse> searchStudentCourseList() {
    return repository.searchStudentCourse().stream()
        //"名前".equals()の形だとNullPointerExceptionを防げるらしい
        .filter(studentCourse -> "Java".equals(studentCourse.getCourseName()))
        .collect(Collectors.toList());
  }

}