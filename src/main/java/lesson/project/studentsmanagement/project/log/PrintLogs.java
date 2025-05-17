package lesson.project.studentsmanagement.project.log;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import lesson.project.studentsmanagement.project.domain.StudentDetail;

public class PrintLogs {

  //確認用のログを表示するクラスはこっちにまとめる
  public void printNotDeletedStudentsAndAllStudentCoursesInStudentList(List<Student> students,
      List<StudentCourse> studentsCourse) {
    //生徒一覧表 取得した論理削除されていない全生徒と、全てのコース情報
    System.out.println("取得した生徒数: " + students.size());
    students.stream().map(student -> "Id:" + student.getId() +
        " 名前:" + student.getName() +
        " フリガナ:" + student.getFurigana() +
        " ニックネーム:" + student.getNickname() +
        " メールアドレス:" + student.getMailAddress() +
        " 地域:" + student.getRegion() +
        " 性別:" + student.getGender() +
        " 備考:" + student.getRemark()).forEach(System.out::println);

    System.out.println("取得したコース数: " + studentsCourse.size());
    studentsCourse.stream().map(studentsCour -> "コースId:" + studentsCour.getId() +
            " 生徒のId:" + studentsCour.getStudentId() +
            " コース名:" + studentsCour.getCourseName() +
            " 開始日程:" + studentsCour.getStartDatetimeAt() +
            " 終了予定日程:" + studentsCour.getPredictedCompleteDatetimeAt())
        .forEach(System.out::println);
  }

  public void printStudentDetail(StudentDetail studentDetail) {
    System.out.println("生徒のId:" + studentDetail.getStudent().getId() + " 名前:"
        + studentDetail.getStudent().getName() + " フリガナ:" + studentDetail.getStudent()
        .getFurigana() + " ニックネーム:" + studentDetail.getStudent().getNickname() + " メールアドレス:"
        + studentDetail.getStudent().getMailAddress() + " 地域:" + studentDetail.getStudent()
        .getRegion() + " 性別:" + studentDetail.getStudent().getGender() + " 備考:"
        + studentDetail.getStudent().getRemark());
    
    studentDetail.getStudentCourseList().stream()
        .map(studentCourse -> "コースId:" + studentCourse.getId() +
            " 生徒のId:" + studentCourse.getStudentId() +
            " コース名:" + studentCourse.getCourseName() +
            " 開始日程:" + studentCourse.getStartDatetimeAt() +
            " 終了予定日程:" + studentCourse
            .getPredictedCompleteDatetimeAt()).forEach(System.out::println);
  }
}
