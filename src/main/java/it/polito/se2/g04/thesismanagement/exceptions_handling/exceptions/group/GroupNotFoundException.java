package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.group;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException(String message) {
        super(message);
    }
}
