package lesson.project.studentsmanagement.project.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import lesson.project.studentsmanagement.project.exception.TestException;
import lesson.project.studentsmanagement.project.log.PrintLogs;
import lesson.project.studentsmanagement.project.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 受講生の登録、更新、削除などを行うREST APIコントローラー。
 */
@RestController
public class StudentController {

  private final StudentService service;
  private final PrintLogs printlogs = new PrintLogs();
  private final Logger logger = LoggerFactory.getLogger(StudentController.class);

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  // ----------- Create -----------

  /**
   * 生徒を登録する。
   *
   * @param studentDetail 登録する生徒情報
   * @return 登録された生徒詳細
   */
  @PostMapping("/registerStudent")
  public ResponseEntity<StudentDetail> registerStudent(
      @RequestBody @Valid StudentDetail studentDetail) {
    logger.info("POSTされた studentDetail: {}", studentDetail);
    StudentDetail registered = service.registerStudent(studentDetail);
    logger.info("作成された生徒:");
    printlogs.printStudentDetail(registered);
    return ResponseEntity.ok(registered);
  }

  // ----------- Read -----------

  /**
   * 全生徒情報を取得する。
   *
   * @return 生徒詳細のリスト
   */
  @GetMapping("/studentList")
  public List<StudentDetail> getStudentList() throws TestException {
    //throw new TestException("XP");
    return service.searchStudentList();
  }

  /**
   * 指定IDの生徒詳細を取得する。
   *
   * @param id 生徒ID
   * @return 生徒詳細
   */
  @GetMapping("/studentDetail/{id}")
  public StudentDetail getStudentDetail(
      @PathVariable @NotNull @NotBlank @Pattern(regexp = "^\\d+$") String id) {
    StudentDetail detail = service.getStudentDetailById(id);
    logger.info("詳細を表示する生徒:");
    printlogs.printStudentDetail(detail);
    return detail;
  }

  // ----------- Update -----------

  /**
   * 生徒更新フォームを表示。
   *
   * @param id 生徒ID
   * @return テンプレート名
   */
  @GetMapping("/updateStudent/{id}")
  public String showUpdateStudentForm(@PathVariable @NotNull String id,
      org.springframework.ui.Model model) {
    StudentDetail studentDetail = service.getStudentDetailById(id);
    model.addAttribute("studentDetail", studentDetail);
    logger.info("更新される生徒:");
    printlogs.printStudentDetail(studentDetail);
    return "updateStudent";
  }

  /**
   * 生徒情報を更新する。 キャンセルフラグ更新もこっち(論理削除)
   *
   * @param studentDetail 更新する生徒詳細
   * @return 実行結果
   */
  @PutMapping("/updateStudent")
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    logger.info("ID {} が更新されました", studentDetail.getStudent().getId());
    return ResponseEntity.ok("こうしんしょりせいこおお");
  }

  // ----------- Delete -----------

  /**
   * 生徒を論理削除する。
   *
   * @param studentDetail 削除対象の生徒
   * @return リダイレクト先
   */
  @PutMapping("/deleteStudent")
  public ResponseEntity<String> deleteStudent(@RequestBody StudentDetail studentDetail) {
    service.logicalDeleteStudent(studentDetail.getStudent());
    logger.info("削除された生徒ID: {}", studentDetail.getStudent().getId());
    return ResponseEntity.ok("さくじょしょりせいこう");
  }

}
