package lesson.project.studentsmanagement.project.log;

import java.util.List;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import lesson.project.studentsmanagement.project.domain.StudentDetail;

public class PrintLogs {

  //確認用のログを表示するクラスはこっちにまとめる
  public void printNotDeletedStudentsAndAllStudentCoursesInStudentList(List<Student> students,
      List<StudentsCourses> studentsCourses) {
    //生徒一覧表 取得した論理削除されていない全生徒と、全てのコース情報
    System.out.println("取得した生徒数: " + students.size());
    for (int i = 0; i < students.size(); i++) {
      System.out.println("Id:" + students.get(i).getId() +
          " 名前:" + students.get(i).getName() +
          " フリガナ:" + students.get(i).getFurigana() +
          " ニックネーム:" + students.get(i).getNickname() +
          " メールアドレス:" + students.get(i).getMailAddress() +
          " 地域:" + students.get(i).getRegion() +
          " 性別:" + students.get(i).getGender() +
          " 備考:" + students.get(i).getRemark());
    }
    System.out.println("取得したコース数: " + studentsCourses.size());
    for (int i = 0; i < studentsCourses.size(); i++) {
      System.out.println("コースId:" + studentsCourses.get(i).getId() +
          " 生徒のId:" + studentsCourses.get(i).getStudentId() +
          " コース名:" + studentsCourses.get(i).getCourseName() +
          " 開始日程:" + studentsCourses.get(i).getStartDatetimeAt() +
          " 終了予定日程:" + studentsCourses.get(i).getPredictedCompleteDatetimeAt());
    }
  }

  public void printStudentDetail(StudentDetail studentDetail) {
    System.out.println("生徒のId:" + studentDetail.getStudent().getId() + " 名前:"
        + studentDetail.getStudent().getName() + " フリガナ:" + studentDetail.getStudent()
        .getFurigana() + " ニックネーム:" + studentDetail.getStudent().getNickname() + " メールアドレス:"
        + studentDetail.getStudent().getMailAddress() + " 地域:" + studentDetail.getStudent()
        .getRegion() + " 性別:" + studentDetail.getStudent().getGender() + " 備考:"
        + studentDetail.getStudent().getRemark());
    for (int i = 0; i < studentDetail.getStudentsCourses().size(); i++) {
      System.out.println("コースId:" + studentDetail.getStudentsCourses().get(i).getId() +
          " 生徒のId:" + studentDetail.getStudentsCourses().get(i).getStudentId() +
          " コース名:" + studentDetail.getStudentsCourses().get(i).getCourseName() +
          " 開始日程:" + studentDetail.getStudentsCourses().get(i).getStartDatetimeAt() +
          " 終了予定日程:" + studentDetail.getStudentsCourses().get(i)
          .getPredictedCompleteDatetimeAt());
    }
  }
}
