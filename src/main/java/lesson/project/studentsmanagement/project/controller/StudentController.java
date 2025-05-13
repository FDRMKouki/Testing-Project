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

/**
 * 受講生の登録、更新、削除などを行うREST APIとして実行されるコントローラー
 */
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

  //生徒の登録 CREATE
//---------------

  /**
   * 登録処理
   *
   * @param studentDetail 生徒詳細
   * @param model         モデル
   * @return　登録された生徒の詳細
   */
  //生徒登録のPost
  @PostMapping("/registerStudent")
  public ResponseEntity<StudentDetail> registerStudent(@RequestBody StudentDetail studentDetail,
      Model model) {
    System.out.println("POSTされた studentDetail: " + studentDetail);
    // 登録サービス呼び出し
    StudentDetail responseStudentDetail = service.registerStudent(studentDetail);
    //確認用ログ
    System.out.println("作成された生徒:");
    printlogs.printStudentDetail(studentDetail);
    //リダイレクト
    return ResponseEntity.ok(responseStudentDetail);
  }

  //生徒の表示系 READ
//---------------

  /**
   * 削除されていない全ての生徒情報を取得する全件検索。
   *
   * @return 受講生(全件)
   */
  @GetMapping("/studentList")
  public List<StudentDetail> getStudentList() {
    List<Student> students = service.searchStudentList();
    List<StudentsCourses> studentsCourses = service.searchStudentsCourseList();
    // 確認用ログ
    printlogs.printNotDeletedStudentsAndAllStudentCoursesInStudentList(students, studentsCourses);
    // コンバーターで変換し、JSONとして返却
    return converter.convertStudentDetails(students, studentsCourses);
  }

  /**
   * 単一の生徒情報を取得する。 コース情報はその受講生のIDに紐づくものを持ってくるようにする
   *
   * @param id 受講生ID
   * @return そのIDの受講生の詳細
   */
  @GetMapping("/studentDetail/{id}")
  public StudentDetail getStudentDetail(@PathVariable String id) {
    System.out.println("詳細を表示する生徒:");
    //確認用ログ
    printlogs.printStudentDetail(service.getStudentDetailById(id));
    return service.getStudentDetailById(id);
  }

  /**
   * (生徒コース情報を取得する)
   *
   * @return 全てのコース情報
   */
  @GetMapping("/studentCourseList")
  public List<StudentsCourses> getStudentCourseList() {
    return service.searchStudentsCourseList();
  }

  //生徒の更新 UPDATE
//---------------

  @GetMapping("/updateStudent/{id}")//特定の生徒の更新画面
  public String showUpdateStudentForm(@PathVariable String id, Model model) {
    StudentDetail studentDetail = service.getStudentDetailById(id);
    model.addAttribute("studentDetail", studentDetail);
    //確認用ログ
    System.out.println("更新される生徒:");
    printlogs.printStudentDetail(studentDetail);
    return "updateStudent";
  }

  /**
   * 生徒の更新
   *
   * @param studentDetail 受講生詳細
   * @return 受講生詳細更新が成功しました
   */
  @PostMapping("/updateStudent")
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    System.out.println("Id:" + studentDetail.getStudent().getId() + "が更新されました");
    return ResponseEntity.ok("こうしんしょりせいこおお");
  }

//生徒の削除 DELETE
//---------------

  /**
   * 論理削除処理 DELETE
   *
   * @param studentDetail 受講生詳細
   * @param result
   * @return
   */
  @PostMapping("/deleteStudent")
  public String deleteStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {
    System.out.println("削除された生徒Id:" + studentDetail.getStudent().getId());
    service.logicalDeleteStudent(studentDetail.getStudent());
    return "redirect:/studentList";
  }
}
