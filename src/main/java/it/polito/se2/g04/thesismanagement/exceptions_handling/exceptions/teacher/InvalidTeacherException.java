package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.teacher;

public class InvalidTeacherException extends RuntimeException{
    public InvalidTeacherException (String message){
        super(message);
    }
}
