package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.degree;

public class DegreeNotFoundException extends RuntimeException {
    public DegreeNotFoundException(String message) {
        super(message);
    }
}
