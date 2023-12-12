package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Application;

public class ApplicationBadRequestFormatException extends RuntimeException {
    public ApplicationBadRequestFormatException(String message) {
        super(message);
    }
}