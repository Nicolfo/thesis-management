package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Group;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException(String message) {
        super(message);
    }
}
