package lesson.project.studentsmanagement.project.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lesson.project.studentsmanagement.project.data.Student;
import lesson.project.studentsmanagement.project.data.StudentsCourses;
import lesson.project.studentsmanagement.project.domain.StudentDetail;
import org.springframework.stereotype.Component;

@Component
public class StudentConverter {

  //生徒詳細変換メゾット(コンバーター)
  public List<StudentDetail> convertStudentDetails(List<Student> students,
      List<StudentsCourses> studentsCourses) {

    //StudentDetail型の生徒詳細リスト作成
    List<StudentDetail> studentDetails = new ArrayList<>();
    //(studentの数だけ繰り返し)
    for (Student student : students) {

      //論理削除されていない生徒のみ
      if (!student.isDeleted()) {
        //StudentDetail型の変数作成、指定の生徒情報を入れる(1人)
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(student);

        //StudentCourse型の変換用リスト作成。ここにIDが一致するコース情報を全て入れる
        //streamAPIを使ってstudentのIDがstudentCourseのstudentIDと一致するコース情報を変換用リストに追加
        List<StudentsCourses> convertStudentCourses = studentsCourses.stream()
            .filter(studentCourse -> student.getId().equals(studentCourse.getStudentId()))
            .collect(Collectors.toList());

        //全コース追加完了 studentDetailに変換用リスト内のコース情報を入れる
        studentDetail.setStudentsCourses(convertStudentCourses);
        //詳細リストに全てのstudentDetail(student✓,studentCourses✓)を入れる。これである1つの生徒詳細完成
        studentDetails.add(studentDetail);
      }

    }
    //全生徒詳細を返す
    return studentDetails;
  }

}
