package lesson.project.studentsmanagement.project.service;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
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
    return repository.searchStudent();
  }

  public List<StudentsCourses> searchStudentsCourseList() {
    return repository.searchStudentCourse();
  }

}