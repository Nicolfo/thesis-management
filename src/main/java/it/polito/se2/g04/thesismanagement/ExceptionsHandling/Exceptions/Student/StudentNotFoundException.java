package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Student;

public class StudentNotFoundException extends RuntimeException{
    public StudentNotFoundException(String message) {
        super(message);
    }
}
