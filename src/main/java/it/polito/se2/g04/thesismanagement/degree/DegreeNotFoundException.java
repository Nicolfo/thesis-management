package it.polito.se2.g04.thesismanagement.degree;

public class DegreeNotFoundException extends RuntimeException {
    public DegreeNotFoundException(String message) {
        super(message);
    }
}
