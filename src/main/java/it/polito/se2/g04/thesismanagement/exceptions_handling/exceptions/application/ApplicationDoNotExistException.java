package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.application;

public class ApplicationDoNotExistException extends RuntimeException{
    public ApplicationDoNotExistException(String message) {
        super(message);
    }
}
