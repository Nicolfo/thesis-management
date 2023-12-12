package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Department;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(String message) {
        super(message);
    }
}
