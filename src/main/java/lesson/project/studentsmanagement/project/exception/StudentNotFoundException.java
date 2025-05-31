package lesson.project.studentsmanagement.project.exception;

public class StudentNotFoundException extends RuntimeException {

  public StudentNotFoundException(String message) {
    super(message);
  }
}