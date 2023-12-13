package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.teacher;

public class TeacherNotFoundException extends RuntimeException{
    public TeacherNotFoundException(String message) {
        super(message);
    }
}
