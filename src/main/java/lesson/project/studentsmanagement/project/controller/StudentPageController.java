package lesson.project.studentsmanagement.project.controller;

import java.util.List;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import lesson.project.studentsmanagement.project.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class StudentPageController {

  private final StudentService service;

  @Autowired
  public StudentPageController(StudentService service) {
    this.service = service;
  }

  // ===== HTML表示用 =====

  // 一覧表示（テンプレート）
  @GetMapping("/studentListPage")
  public String showStudentList(Model model) {
    List<StudentDetail> studentList = service.searchStudentList();
    model.addAttribute("studentList", studentList);
    return "studentList";
  }

  // 詳細表示（テンプレート）
  @GetMapping("/studentDetailPage/{id}")
  public String showStudentDetail(@PathVariable("id") String id, Model model) {
    StudentDetail detail = service.getStudentDetailById(id);
    model.addAttribute("studentDetail", detail);
    return "studentDetail";
  }

  // 更新フォーム表示（テンプレート）
  @GetMapping("/updateStudentPage/{id}")
  public String showUpdateStudentForm(@PathVariable("id") String id, Model model) {
    StudentDetail studentDetail = service.getStudentDetailById(id);
    model.addAttribute("studentDetail", studentDetail);
    return "updateStudent";
  }

}
