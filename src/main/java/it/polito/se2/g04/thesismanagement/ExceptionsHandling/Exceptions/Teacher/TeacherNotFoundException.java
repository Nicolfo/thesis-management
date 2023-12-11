package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Teacher;

public class TeacherNotFoundException extends RuntimeException{
    public TeacherNotFoundException(String message) {
        super(message);
    }
}
