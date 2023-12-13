package it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.teacher;

public class TeacherNotFoundException extends RuntimeException{
    public TeacherNotFoundException(String message) {
        super(message);
    }
}
