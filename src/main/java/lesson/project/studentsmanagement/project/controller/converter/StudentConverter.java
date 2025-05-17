package lesson.project.studentsmanagement.project.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentCourse;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import org.springframework.stereotype.Component;

/**
 * 受講生情報(Student)とコース情報(StudentCourses)を合わせて受講生詳細(StudentDetail)に変換する。
 * 受講生詳細の箱の中に特定のIDの生徒とそのIDと紐づくコース情報を入れて包装するイメージ
 */
@Component
public class StudentConverter {

  /**
   * 受講生に紐づくコース情報のマッピング。 コースは複数ある可能性もあるのでその数だけ繰り返し
   *
   * @param studentList       受講生一覧
   * @param studentCourseList コース情報
   * @return 受講生詳細情報リスト
   */

  //生徒詳細変換メゾット(コンバーター)
  public List<StudentDetail> convertStudentDetails(List<Student> studentList,
      List<StudentCourse> studentCourseList) {

    //StudentDetail型の生徒詳細リスト作成
    List<StudentDetail> studentDetails = new ArrayList<>();
    //(studentの数だけ繰り返し)
    for (Student student : studentList) {

      //論理削除されていない生徒のみ
      if (!student.isDeleted()) {
        //StudentDetail型の変数作成、指定の生徒の情報を入れる(1人)
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(student);

        //StudentCourse型の変換用リスト作成。ここにIDが一致するコース情報を全て入れる
        //streamAPIを使ってstudentのIDがstudentCourseのstudentIDと一致するコース情報を変換用リストに追加
        List<StudentCourse> convertStudentCourseList = studentCourseList.stream()
            .filter(studentCourse -> student.getId().equals(studentCourse.getStudentId()))
            .collect(Collectors.toList());

        //全コース追加完了 studentDetailに変換用リスト内のコース情報を入れる
        studentDetail.setStudentCourseList(convertStudentCourseList);
        //詳細リストに全てのstudentDetail(student✓,studentCourses✓)を入れる。これである1つの生徒詳細完成
        studentDetails.add(studentDetail);
      }

    }
    //全生徒詳細を返す
    return studentDetails;
  }

}
