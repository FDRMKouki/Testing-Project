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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lesson.project.studentsmanagement.project.data.CourseStatus;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import lesson.project.studentsmanagement.project.service.StudentService;
import lesson.project.studentsmanagement.project.validation.UpdateGroup;
import org.junit.jupiter.api.BeforeEach;
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

  @BeforeEach
  void setup() {
    Mockito.reset(service);
  }
  //data.sqlと干渉しないテストです
  // ========================================================
  // ============== 入力バリデーション関連のテスト ==========
  // ========================================================

  @Test
  void 受講生詳細の受講生で適切な値を入力したときに入力チェックに異常が発生しないことのテスト() {
    Student student = new Student(
        123214L, "てきせつあいでぃー太郎", "テキセツアイディータロウ", "適切なIDを持ってる人",
        "shiruka@example", "不明県", 20,
        "Who said the name Taro is always man's name?",
        null, false
    );

    Set<ConstraintViolation<Student>> violations = validator.validate(student, UpdateGroup.class);

    //正常に入力したならそりゃエラー出ないでしょ
    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生詳細の受講生でIDに数字以外を用いた時に入力チェックに引っかかることのテスト() {
    Student student = new Student(
        null, "のっとてきせつ太郎", "ノットテキセツタロウ", "適切でないIDを持ってる人",
        "shiruka@example", "不明県", 20,
        "Who said the name Taro is always man's name?",
        null, false//必要な時123214Lにする(かも)
    );

    Set<ConstraintViolation<Student>> violations = validator.validate(student, UpdateGroup.class);

    //Studentの値のうちIDに関するテストなので 1つだけ問題を取得するように
    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message").containsOnly(
        "IDは必須です");//コイツはStudent.java内の@NotNull(message = "ここ")の「ここ」がcontainsOnly("ここ")の「ここ」と一致させるべき
  }

  @Test
  void StudentDetailのバリデーション_StudentとCourseの必須項目が不足していたらエラーが出ることのテスト() {
    Student student = new Student(null, null, null, null, null, null, 0, null, null,
        false); // 空のコンストラクタ
    StudentCourse course = new StudentCourse(null, null, null, null, null); // 空のコンストラクタ
    CourseStatus status = new CourseStatus(null, 0); // 空のコンストラクタ
    StudentDetail detail = new StudentDetail(student, List.of(course), List.of(status));

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
    Student student = new Student(
        123L, "詳細太郎", "ショウサイタロウ", "しょうたろ",
        "taro@example.com", "東京", 21,
        "男性", null, false
    );
    StudentDetail detail = new StudentDetail(student, List.of(), List.of());

    Mockito.when(service.getStudentDetailById(id)).thenReturn(detail);

    mockMvc.perform(get("/studentDetail/" + id))
        .andExpect(status().isOk());
  }

  @Test
  void 特定のIDの受講生詳細と受講コースリストが取得できることのテスト() throws Exception {
    String id = "123";

    Student student = new Student(
        123L, "詳細太郎", "ショウサイタロウ", "しょうたろ",
        "taro@example.com", "東京", 21,
        "男性", null, false
    );

    StudentCourse course1 = new StudentCourse(1L, 123L, "Java",
        LocalDateTime.of(2025, 7, 1, 10, 0),
        LocalDateTime.of(2025, 9, 1, 18, 0)
    );
    StudentCourse course2 = new StudentCourse(2L, 123L, "Spring Boot",
        LocalDateTime.of(2025, 7, 2, 10, 0),
        LocalDateTime.of(2025, 9, 2, 18, 0)
    );
    CourseStatus status1 = new CourseStatus(1L, 1);
    CourseStatus status2 = new CourseStatus(2L, 1);

    // StudentDetail にセット
    StudentDetail detail = new StudentDetail(student, List.of(course1, course2),
        List.of(status1, status2));

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
    Student student = new Student(
        1L, "新規太郎", "シンキタロウ", "しんたろ",
        "taro@example.com", "東京", 25,
        "男性", null, false
    );
    StudentDetail detail = new StudentDetail(student, List.of(), List.of());

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

  @Test
  void 複数のコースを持つ生徒が正しく登録できているかのテスト() throws Exception {
    String requestJson = """
        {
          "student": {
            "id": 10,
            "name": "複数コース太郎",
            "furigana": "フクスウコースタロウ",
            "nickname": "multi",
            "mailAddress": "multi@example.com",
            "region": "東京",
            "gender": "男性",
            "age": 25
          },
          "studentCourseList": [
            {
              "id": 1,
              "studentId": 10,
              "courseName": "Java",
              "startDatetimeAt": "2025-07-01T10:00:00",
              "predictedCompleteDatetimeAt": "2025-09-30T18:00:00"
            },
            {
              "id": 2,
              "studentId": 10,
              "courseName": "Spring Boot",
              "startDatetimeAt": "2025-07-15T10:00:00",
              "predictedCompleteDatetimeAt": "2025-10-30T18:00:00"
            }
          ]
        }
        """;

    // モックの戻り値を適当に作成（必要に応じて詳細情報もセット）
    Student student = new Student(10L, "複数コース太郎", "フクスウコースタロウ", "multi",
        "multi@example.com", "東京", 25, "男性", null, false);
    StudentCourse course1 = new StudentCourse(1L, 10L, "Java",
        LocalDateTime.of(2025, 7, 1, 10, 0),
        LocalDateTime.of(2025, 9, 30, 18, 0));
    StudentCourse course2 = new StudentCourse(2L, 10L, "Spring Boot",
        LocalDateTime.of(2025, 7, 15, 10, 0),
        LocalDateTime.of(2025, 10, 30, 18, 0));
    StudentDetail returnedDetail = new StudentDetail(student, List.of(course1, course2), List.of());

    Mockito.when(service.registerStudent(Mockito.any(StudentDetail.class)))
        .thenReturn(returnedDetail);

    mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/registerStudent")
                .contentType("application/json")
                .content(requestJson)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.name").value("複数コース太郎"))
        .andExpect(jsonPath("$.studentCourseList").isArray())
        .andExpect(jsonPath("$.studentCourseList.length()").value(2))
        .andExpect(jsonPath("$.studentCourseList[0].courseName").value("Java"))
        .andExpect(jsonPath("$.studentCourseList[1].courseName").value("Spring Boot"));
  }

  // ========================================================
  // =================== PUT系 REST APIのテスト =============
  // ========================================================

  @Test
  void PUTで受講生情報が更新できることのテスト() throws Exception {
    Student student = new Student(
        1L, "更新太郎", "コウシンタロウ", "こうたろ",
        "koushin@example.com", "大阪", 30,
        "男性", null, false
    );

    StudentDetail detail = new StudentDetail(student, List.of(), List.of());

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

  @Test
  void 複数のコースを持つ生徒が正しく更新できているかのテスト() throws Exception {
    String requestJson = """
        {
          "student": {
            "id": 10,
            "name": "更新コース太郎",
            "furigana": "コウシンコースタロウ",
            "nickname": "update",
            "mailAddress": "update@example.com",
            "region": "大阪",
            "gender": "男性",
            "age": 30
          },
          "studentCourseList": [
            {
              "id": 1,
              "studentId": 10,
              "courseName": "Python",
              "startDatetimeAt": "2025-08-01T10:00:00",
              "predictedCompleteDatetimeAt": "2025-10-31T18:00:00"
            },
            {
              "id": 2,
              "studentId": 10,
              "courseName": "React",
              "startDatetimeAt": "2025-08-15T10:00:00",
              "predictedCompleteDatetimeAt": "2025-11-30T18:00:00"
            }
          ]
        }
        """;

    Student student = new Student(10L, "更新コース太郎", "コウシンコースタロウ", "update",
        "update@example.com", "大阪", 30, "男性", null, false);
    StudentCourse course1 = new StudentCourse(1L, 10L, "Python",
        LocalDateTime.of(2025, 8, 1, 10, 0),
        LocalDateTime.of(2025, 10, 31, 18, 0));
    StudentCourse course2 = new StudentCourse(2L, 10L, "React",
        LocalDateTime.of(2025, 8, 15, 10, 0),
        LocalDateTime.of(2025, 11, 30, 18, 0));
    StudentDetail returnedDetail = new StudentDetail(student, List.of(course1, course2), List.of());

    Mockito.doNothing().when(service).updateStudent(Mockito.any(StudentDetail.class));
    Mockito.when(service.getStudentDetailById("10")).thenReturn(returnedDetail);

    mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/updateStudent")
                .contentType("application/json")
                .content(requestJson)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.name").value("更新コース太郎"))
        .andExpect(jsonPath("$.studentCourseList").isArray())
        .andExpect(jsonPath("$.studentCourseList.length()").value(2))
        .andExpect(jsonPath("$.studentCourseList[0].courseName").value("Python"))
        .andExpect(jsonPath("$.studentCourseList[1].courseName").value("React"));

    // updateStudentメソッドが1回呼ばれているか検証
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