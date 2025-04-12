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

  //Read 生徒コース情報を取得するcurl "http://localhost:8080/studentCourseList"
  @GetMapping("/studentCourseList")
  public List<StudentsCourses> getStudentCourseList() {
    return service.searchStudentsCourseList();
  }

  //新規生徒登録画面 registerStudent.htmlを返す
  @GetMapping("/newStudent")
  public String newStudent(Model model) {
    model.addAttribute("studentDetail", new StudentDetail());
    return "registerStudent";
  }

  //生徒登録画面で登録ボタンが押されたとき
  @PostMapping("/registerStudent")
  public String registerStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {

    System.out.println(studentDetail.getStudent());
    //service.insertDefaultCourseForStudent(studentDetail.getStudent().getId());
    if (result.hasErrors()) {
      System.out.println("エラーが発生しました");
      return "registerStudent";
    }
    //studentDetail.getStudent()変数を受け継いで登録サービス呼び出し
    //※仮にIdも自分で登録できるようにしています
    service.registerStudent(studentDetail.getStudent());
    //仮のコース情報登録サービス呼び出し

    //確認用ログ
    System.out.println("登録された生徒のId:" + studentDetail.getStudent().getId() + " 名前:"
        + studentDetail.getStudent().getName() + " フリガナ:" + studentDetail.getStudent()
        .getFurigana() + " ニックネーム:" + studentDetail.getStudent().getNickname() + " メールアドレス:"
        + studentDetail.getStudent().getMailAddress() + " 地域:" + studentDetail.getStudent()
        .getRegion() + " 性別:" + studentDetail.getStudent().getGender() + " 備考:"
        + studentDetail.getStudent().getRemark());
    //リダイレクト
    return "redirect:/studentList";
  }
}
