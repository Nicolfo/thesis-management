package it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.department;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(String message) {
        super(message);
    }
}
