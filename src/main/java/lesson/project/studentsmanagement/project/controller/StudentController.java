package lesson.project.studentsmanagement.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import lesson.project.studentsmanagement.project.exception.TestException;
import lesson.project.studentsmanagement.project.log.PrintLogs;
import lesson.project.studentsmanagement.project.service.StudentService;
import lesson.project.studentsmanagement.project.validation.CreateGroup;
import lesson.project.studentsmanagement.project.validation.UpdateGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
  @Operation(summary = "受講生登録", description = "受講生を登録する。")
  @PostMapping("/registerStudent")
  public ResponseEntity<StudentDetail> registerStudent(
      @RequestBody @Validated(CreateGroup.class) StudentDetail studentDetail) {
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
  @Operation(summary = "一覧検索", description = "受講生の一覧を検索する。")
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
  @Operation(summary = "受講生詳細検索", description = "特定のIDの受講生を検索する。")
  @GetMapping("/studentDetail/{id}")
  public StudentDetail getStudentDetail(
      @PathVariable @NotNull @NotBlank @Pattern(regexp = "^\\d+$") String id) {
    StudentDetail detail = service.getStudentDetailById(id);
    logger.info("詳細を表示する生徒:");
    printlogs.printStudentDetail(detail);
    return detail;
  }

  /**
   * 条件で生徒を検索（名前・ふりがな・メールアドレスの部分一致）。
   *
   * @param name        名前（部分一致）
   * @param furigana    フリガナ（部分一致）
   * @param mailAddress メールアドレス（部分一致）
   * @return 検索結果の生徒詳細リスト
   */
  @Operation(summary = "受講生検索（条件指定）", description = "名前・フリガナ・メールアドレスで部分一致検索する。")
  @GetMapping("/searchStudents")
  public List<StudentDetail> searchStudents(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String furigana,
      @RequestParam(required = false) String mailAddress) {

    logger.info("検索条件: name={}, furigana={}, mailAddress={}", name, furigana, mailAddress);
    return service.searchStudents(name, furigana, mailAddress);
  }

  // ----------- Update -----------

  /**
   * 生徒更新フォームを表示。
   *
   * @param id 生徒ID
   * @return テンプレート名
   */
  @Operation(summary = "受講生更新フォーム表示用", description = "受講生を更新するときの画面を表示するためのメゾット。")
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
  @Operation(summary = "受講生更新", description = "特定のIDの受講生詳細を更新する。")
  @PutMapping("/updateStudent")
  public ResponseEntity<StudentDetail> updateStudent(
      @RequestBody @Validated(UpdateGroup.class) StudentDetail studentDetail) {
    if (studentDetail.getStudent() == null || studentDetail.getStudent().getId() == null) {
      throw new IllegalArgumentException("IDは必須です。");
    } else {
      service.updateStudent(studentDetail);
      logger.info("ID {} が更新されました", studentDetail.getStudent().getId());
      StudentDetail updated = service.getStudentDetailById(
          studentDetail.getStudent().getId().toString());
      return ResponseEntity.ok(updated);
    }
  }

  @PostMapping("/updateStudent")
  public String updateStudentHtml(
      @ModelAttribute("studentDetail") @Validated(UpdateGroup.class) StudentDetail studentDetail,
      Model model) {
    service.updateStudent(studentDetail);
    StudentDetail updated = service.getStudentDetailById(
        studentDetail.getStudent().getId().toString());
    model.addAttribute("studentDetail", updated);
    return "redirect:/studentDetailPage/" + studentDetail.getStudent().getId();
  }

  // ----------- Delete -----------

  /**
   * 生徒を論理削除する。
   *
   * @param studentDetail 削除対象の生徒
   * @return リダイレクト先
   */
  @Operation(summary = "受講生削除", description = "特定のIDの受講生を削除する。")
  @PutMapping("/deleteStudent")
  public ResponseEntity<StudentDetail> deleteStudent(@RequestBody StudentDetail studentDetail) {
    service.logicalDeleteStudent(studentDetail.getStudent());
    Long id = studentDetail.getStudent().getId();
    logger.info("削除された生徒ID: {}", id);

    // 削除直後の状態を取得して返す（is_deleted=true）
    StudentDetail deleted = service.getStudentDetailById(id.toString());
    return ResponseEntity.ok(deleted);
  }

}
