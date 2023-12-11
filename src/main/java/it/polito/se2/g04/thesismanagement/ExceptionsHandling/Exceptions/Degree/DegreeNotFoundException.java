package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Degree;

public class DegreeNotFoundException extends RuntimeException {
    public DegreeNotFoundException(String message) {
        super(message);
    }
}
