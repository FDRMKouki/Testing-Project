package lesson.project.studentsmanagement.project.controller;

import java.util.List;
import lesson.project.studentsmanagement.project.controller.converter.StudentConverter;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import lesson.project.studentsmanagement.project.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {

  private StudentService service;
  private StudentConverter converter;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  //Read 生徒情報を取得する curl "http://localhost:8080/studentList"
  @GetMapping("/studentList")
  public String getStudentList(Model model) {
    List<Student> students = service.searchStudentList();
    List<StudentsCourses> studentsCourses = service.searchStudentsCourseList();

    //コンバーター 全生徒追加完了後完成した詳細リストをhtmlのattributeに返す
    model.addAttribute("studentList", converter.convertStudentDetails(students, studentsCourses));
    return "studentList";
  }

  //Read 生徒コース情報を取得するcurl "http://localhost:8080/studentCourseList"
  @GetMapping("/studentCourseList")
  public List<StudentsCourses> getStudentCourseList() {
    return service.searchStudentsCourseList();
  }
}
