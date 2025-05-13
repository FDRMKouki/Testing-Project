package lesson.project.studentsmanagement.project.controller;

import java.util.List;
import lesson.project.studentsmanagement.project.controller.converter.StudentConverter;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import lesson.project.studentsmanagement.project.log.PrintLogs;
import lesson.project.studentsmanagement.project.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {

  private StudentService service;
  private StudentConverter converter;
  private PrintLogs printlogs;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
    this.printlogs = new PrintLogs();
  }

  //----生徒登録----
  //新規生徒登録画面 registerStudent.htmlを返す
  @GetMapping("/newStudent")
  public String newStudent(Model model) {
    StudentDetail studentDetail = new StudentDetail();  // ← ここで初期化済み

    model.addAttribute("studentDetail", studentDetail);
    return "registerStudent";
  }

  //生徒登録画面で登録ボタンが押されたとき
  @PostMapping("/registerStudent")
  public ResponseEntity<String> registerStudent(@RequestBody StudentDetail studentDetail,
      Model model) {
    System.out.println("POSTされた studentDetail: " + studentDetail);

    // 登録サービス呼び出し
    service.registerStudent(studentDetail);

    //確認用ログ
    System.out.println("作成された生徒:");
    printlogs.printStudentDetail(studentDetail);

    //リダイレクト
    return ResponseEntity.ok("とうろくしょりせいこおお");
  }

  //----生徒表示----
  //Read 生徒情報を取得する
  @GetMapping("/studentList")
  public List<StudentDetail> getStudentList() {
    List<Student> students = service.searchStudentList();
    List<StudentsCourses> studentsCourses = service.searchStudentsCourseList();

    // 確認用ログ
    printlogs.printNotDeletedStudentsAndAllStudentCoursesInStudentList(students, studentsCourses);

    // コンバーターで変換し、JSONとして返却
    return converter.convertStudentDetails(students, studentsCourses);
  }

  //名前をクリックされた生徒情報の表示
  @GetMapping("/studentDetail/{id}")
  public String getStudentDetail(@PathVariable String id, Model model) {
    StudentDetail studentDetail = service.getStudentDetailById(id);
    model.addAttribute("studentDetail", studentDetail);
    System.out.println("クリックされた生徒:");
    //確認用ログ
    printlogs.printStudentDetail(studentDetail);
    return "studentDetail";
  }

  //Read 生徒コース情報を取得する
  @GetMapping("/studentCourseList")
  public List<StudentsCourses> getStudentCourseList() {
    return service.searchStudentsCourseList();
  }

  //----生徒更新----
  @GetMapping("/updateStudent/{id}")//特定の生徒の更新画面
  public String showUpdateStudentForm(@PathVariable String id, Model model) {
    StudentDetail studentDetail = service.getStudentDetailById(id);
    model.addAttribute("studentDetail", studentDetail);
    //確認用ログ
    System.out.println("更新される生徒:");
    printlogs.printStudentDetail(studentDetail);
    return "updateStudent";
  }

  @PostMapping("/updateStudent")//更新ボタン押されたとき。
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    System.out.println("Id:" + studentDetail.getStudent().getId() + "が更新されました");
    //return "redirect:/studentDetail/" + studentDetail.getStudent().getId();
    return ResponseEntity.ok("こうしんしょりせいこおお");
  }

  //----生徒論理削除-----
  @PostMapping("/deleteStudent")
  public String deleteStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {
    System.out.println("削除された生徒Id:" + studentDetail.getStudent().getId());
    service.logicalDeleteStudent(studentDetail.getStudent());
    return "redirect:/studentList";
  }
}
