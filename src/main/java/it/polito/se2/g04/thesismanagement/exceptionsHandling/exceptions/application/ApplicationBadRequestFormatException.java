package it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.application;

public class ApplicationBadRequestFormatException extends RuntimeException {
    public ApplicationBadRequestFormatException(String message) {
        super(message);
    }
}