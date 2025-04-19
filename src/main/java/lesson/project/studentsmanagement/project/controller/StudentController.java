package lesson.project.studentsmanagement.project.controller;

import java.util.List;
import lesson.project.studentsmanagement.project.controller.converter.StudentConverter;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import lesson.project.studentsmanagement.project.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StudentController {

  private StudentService service;
  private StudentConverter converter;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  //----生徒登録----
  //新規生徒登録画面 registerStudent.htmlを返すcurl "http://localhost:8080/newStudent"
  @GetMapping("/newStudent")
  public String newStudent(Model model) {
    StudentDetail studentDetail = new StudentDetail();  // ← ここで初期化済み

    model.addAttribute("studentDetail", studentDetail);
    return "registerStudent";
  }

  //生徒登録画面で登録ボタンが押されたとき
  @PostMapping("/registerStudent")
  public String registerStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result,
      Model model) {
    System.out.println("POSTされた studentDetail: " + studentDetail);

    if (result.hasErrors()) {
      // 🔴 これを忘れてると NullPointerException になる
      model.addAttribute("studentDetail", studentDetail);
      return "registerStudent";
    }

    // 登録サービス呼び出し
    service.registerStudent(studentDetail);

    //確認用ログ
    System.out.println("登録された生徒の全体情報");
    System.out.println(studentDetail);
    System.out.println("登録された生徒の個人の情報");
    System.out.println("登録された生徒のId:" + studentDetail.getStudent().getId() + " 名前:"
        + studentDetail.getStudent().getName() + " フリガナ:" + studentDetail.getStudent()
        .getFurigana() + " ニックネーム:" + studentDetail.getStudent().getNickname() + " メールアドレス:"
        + studentDetail.getStudent().getMailAddress() + " 地域:" + studentDetail.getStudent()
        .getRegion() + " 性別:" + studentDetail.getStudent().getGender() + " 備考:"
        + studentDetail.getStudent().getRemark());

    //リダイレクト
    return "redirect:/studentList";
  }

  //----生徒表示----
  //Read 生徒情報を取得する curl "http://localhost:8080/studentList"
  @GetMapping("/studentList")
  public String getStudentList(Model model) {
    List<Student> students = service.searchStudentList();
    List<StudentsCourses> studentsCourses = service.searchStudentsCourseList();

    System.out.println("取得した生徒数: " + students.size());
    System.out.println("取得したコース数: " + studentsCourses.size());

    //コンバーター 全生徒追加完了後完成した詳細リストをhtmlのattributeに返す
    model.addAttribute("studentList", converter.convertStudentDetails(students, studentsCourses));
    return "studentList";
  }

  //名前をクリックされた生徒情報の表示
  @GetMapping("/studentDetail/{id}")
  public String getStudentDetail(@PathVariable String id, Model model) {
    StudentDetail studentDetail = service.getStudentDetailById(id);
    model.addAttribute("studentDetail", studentDetail);
    return "studentDetail";
  }

  //Read 生徒コース情報を取得するcurl "http://localhost:8080/studentCourseList"
  @GetMapping("/studentCourseList")
  public List<StudentsCourses> getStudentCourseList() {
    return service.searchStudentsCourseList();
  }

  //----生徒更新----
  @GetMapping("/updateStudent/{id}")
  public String showUpdateStudentForm(@PathVariable String id, Model model) {
    StudentDetail studentDetail = service.getStudentDetailById(id);
    model.addAttribute("studentDetail", studentDetail);
    return "updateStudent";
  }

  @PostMapping("/updateStudent")
  public String updateStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {
    if (result.hasErrors()) {
      return "updateStudent";
    }

    service.updateStudent(studentDetail);
    return "redirect:/studentDetail/" + studentDetail.getStudent().getId();
  }
}
