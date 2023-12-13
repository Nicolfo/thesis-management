package it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.degree;

public class DegreeNotFoundException extends RuntimeException {
    public DegreeNotFoundException(String message) {
        super(message);
    }
}
