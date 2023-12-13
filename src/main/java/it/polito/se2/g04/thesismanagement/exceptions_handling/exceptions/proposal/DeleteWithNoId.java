package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal;

public class DeleteWithNoId extends RuntimeException {
    public DeleteWithNoId(String message) {
        super(message);
    }
}