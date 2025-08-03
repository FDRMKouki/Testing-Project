package lesson.project.studentsmanagement.project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import lesson.project.studentsmanagement.project.service.StudentService;
import lesson.project.studentsmanagement.project.validation.CreateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StudentPageController {

  private final StudentService service;

  @Autowired
  public StudentPageController(StudentService service) {
    this.service = service;
  }

  // ===== HTML表示用 =====
  @GetMapping("/registerStudentPage")
  public String showRegisterStudentForm(Model model) {
    StudentDetail studentDetail = new StudentDetail();

    // 空のリストで初期化（← これがポイント！）
    studentDetail.setStudentCourseList(new ArrayList<>());
    Student student = new Student();
    studentDetail.setStudent(student);

    model.addAttribute("studentDetail", studentDetail);
    return "registerStudent";
  }

  @PostMapping("/registerStudentPage")
  public String registerStudent(
      @Validated(CreateGroup.class) @ModelAttribute("studentDetail") StudentDetail studentDetail,
      BindingResult bindingResult,
      Model model
  ) {
    if (bindingResult.hasErrors()) {
      return "registerStudent";
    }

    service.registerStudent(studentDetail);
    return "redirect:/studentListPage";
  }

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

    // 安全な null 対策（再確認）
    if (studentDetail.getStudentCourseList() == null) {
      studentDetail.setStudentCourseList(new ArrayList<>());
    }

    for (StudentCourse course : studentDetail.getStudentCourseList()) {
      if (course.getAppStatus() == null) {
        course.setAppStatus("1"); // 仮申込
      }
    }

    Map<Integer, String> statusOptions = Map.of(
        1, "仮申込",
        2, "本申込",
        3, "受講中",
        4, "受講終了"
    );

    model.addAttribute("studentDetail", studentDetail);
    model.addAttribute("statusOptions", statusOptions);
    return "updateStudent";
  }

  //更新フォームで更新ボタン押されたときの処理
  @PostMapping("/updateStudentPage")
  public String updateStudentPage(@ModelAttribute StudentDetail studentDetail) {
    // Serviceで更新
    service.updateStudent(studentDetail);

    // 画面遷移用に redirect 返却
    return "redirect:/studentDetailPage/" + studentDetail.getStudent().getId();
  }


  @PostMapping("/deleteStudent")
  public String deleteStudentHtml(@ModelAttribute("studentDetail") StudentDetail studentDetail) {
    service.logicalDeleteStudent(studentDetail.getStudent());
    return "redirect:/studentListPage";
  }


}
