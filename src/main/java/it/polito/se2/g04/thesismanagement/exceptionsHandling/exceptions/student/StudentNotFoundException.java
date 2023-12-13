package it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.student;

public class StudentNotFoundException extends RuntimeException{
    public StudentNotFoundException(String message) {
        super(message);
    }
}
