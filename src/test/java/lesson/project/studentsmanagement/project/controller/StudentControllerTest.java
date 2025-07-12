package lesson.project.studentsmanagement.project.controller;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import lesson.project.studentsmanagement.project.service.StudentService;
import lesson.project.studentsmanagement.project.validation.UpdateGroup;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

  private static final Logger log = LoggerFactory.getLogger(StudentControllerTest.class);
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  // ========================================================
  // ============== 入力バリデーション関連のテスト ==========
  // ========================================================

  @Test
  void 受講生詳細の受講生で適切な値を入力したときに入力チェックに異常が発生しないことのテスト() {
    Student student = new Student();
    student.setId(123214L);
    student.setName("のっとてきせつ太郎");
    student.setFurigana("ノットテキセツタロウ");
    student.setNickname("適切でないIDを持ってる人");
    student.setAge(20);
    student.setMailAddress("shiruka@example");
    student.setRegion("不明県");
    student.setGender("Who said the name Taro is always man's name?");

    Set<ConstraintViolation<Student>> violations = validator.validate(student, UpdateGroup.class);

    //正常に入力したならそりゃエラー出ないでしょ
    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生詳細の受講生でIDに数字以外を用いた時に入力チェックに引っかかることのテスト() {
    Student student = new Student();
    student.setId(null);
    student.setName("のっとてきせつ太郎");
    student.setFurigana("ノットテキセツタロウ");
    student.setNickname("適切でないIDを持ってる人");
    student.setAge(20);
    student.setMailAddress("shiruka@example");
    student.setRegion("不明県");
    student.setGender("Who said the name Taro is always man's name?");

    Set<ConstraintViolation<Student>> violations = validator.validate(student, UpdateGroup.class);

    //Studentの値のうちIDに関するテストなので 1つだけ問題を取得するように
    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message").containsOnly(
        "IDは必須です");//コイツはStudent.java内の@NotNull(message = "ここ")の「ここ」がcontainsOnly("ここ")の「ここ」と一致させるべき
  }

  @Test
  void StudentDetailのバリデーション_StudentとCourseの必須項目が不足していたらエラーが出ることのテスト() {
    // 必須項目が未設定の Student
    Student student = new Student();

    // 必須項目が未設定の StudentCourse
    StudentCourse course = new StudentCourse();

    StudentDetail detail = new StudentDetail(student, List.of(course));

    Set<ConstraintViolation<StudentDetail>> violations = validator.validate(detail,
        UpdateGroup.class);

    // 検証：ネストされたオブジェクトでもエラーが検出される
    assertThat(violations).isNotEmpty();
    assertThat(violations)
        .extracting("message")
        .contains("IDは必須です", "名前は空白にできません", "フリガナは空白にできません",
            "ニックネームは空白にできません", "メールアドレスは空白にできません",
            "都道府県は空白にできません", "性別は空白にできません"); // 必要に応じてカスタマイズ
  }

  // ========================================================
  // =================== GET系 REST APIのテスト =============
  // ========================================================

  @Test
  void 受講生情報の一覧検索の実行ができ空のリストが返ってくることのテスト() throws Exception {
    //RestAPIとして機能しているかのテスト
    Mockito.when(service.searchStudentList()).thenReturn(List.of());

    mockMvc.perform(get("/studentList"))
        .andExpect(status().isOk());
  }

  @Test
  void 特定IDの受講生詳細が取得できることのテスト() throws Exception {
    String id = "123";
    Student student = new Student();
    student.setId(123L);
    student.setName("詳細太郎");
    StudentDetail detail = new StudentDetail(student, List.of());

    Mockito.when(service.getStudentDetailById(id)).thenReturn(detail);

    mockMvc.perform(get("/studentDetail/" + id))
        .andExpect(status().isOk());
  }

  @Test
  void 特定IDの受講生詳細と受講コースリストが取得できることのテスト() throws Exception {
    String id = "123";

    // Student 作成
    Student student = new Student();
    student.setId(123L);
    student.setName("詳細太郎");
    student.setFurigana("ショウサイタロウ");
    student.setNickname("しょうたろ");
    student.setAge(21);
    student.setMailAddress("taro@example.com");
    student.setRegion("東京");
    student.setGender("男性");

    // StudentCourse 作成
    StudentCourse course1 = new StudentCourse();
    course1.setCourseName("Java");
    StudentCourse course2 = new StudentCourse();
    course2.setCourseName("Spring Boot");

    // StudentDetail にセット
    StudentDetail detail = new StudentDetail(student, List.of(course1, course2));

    // モック定義
    Mockito.when(service.getStudentDetailById(id)).thenReturn(detail);

    // 実行 & 検証
    mockMvc.perform(get("/studentDetail/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.name").value("詳細太郎"))
        .andExpect(jsonPath("$.studentCourseList[0].courseName").value("Java"))
        .andExpect(jsonPath("$.studentCourseList[1].courseName").value("Spring Boot"));
  }

  // ========================================================
  // =================== POST系 REST APIのテスト ============
  // ========================================================

  @Test
  void 受講生登録のPOSTがRESTAPIとして動作し200が返ることのテスト() throws Exception {
    Student student = new Student();
    student.setId(1L);
    student.setName("新規太郎");

    StudentDetail detail = new StudentDetail(student, List.of());

    Mockito.when(service.registerStudent(Mockito.any(StudentDetail.class)))
        .thenReturn(detail);

    String requestJson = """
        {
          "student": {
            "id": 1,
            "name": "新規太郎",
            "furigana": "シンキタロウ",
            "nickname": "しんたろ",
            "age": 25,
            "mailAddress": "taro@example.com",
            "region": "東京",
            "gender": "男性"
          },
          "studentCourseList": []
        }
        """;

    mockMvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .post("/registerStudent")
            .contentType("application/json")
            .content(requestJson)
    ).andExpect(status().isOk());
  }

  // ========================================================
  // =================== PUT系 REST APIのテスト =============
  // ========================================================

  @Test
  void PUTで受講生情報が更新できることのテスト() throws Exception {
    Student student = new Student();
    student.setId(1L);
    student.setName("更新太郎");

    StudentDetail detail = new StudentDetail(student, List.of());

    String requestJson = """
        {
          "student": {
            "id": 1,
            "name": "更新太郎",
            "furigana": "コウシンタロウ",
            "nickname": "こうたろ",
            "mailAddress": "koushin@example.com",
            "region": "大阪",
            "gender": "男性",
            "age": 30
          },
          "studentCourseList": []
        }
        """;

    mockMvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
            .put("/updateStudent")
            .contentType("application/json")
            .content(requestJson)
    ).andExpect(status().isOk());

    verify(service, times(1)).updateStudent(Mockito.any(StudentDetail.class));
  }

  // ========================================================
  // =================== モック設定 ==========================
  // ========================================================

  @TestConfiguration
  static class MockConfig {

    @Bean
    public StudentService studentService() {
      return Mockito.mock(StudentService.class);
    }
  }

}