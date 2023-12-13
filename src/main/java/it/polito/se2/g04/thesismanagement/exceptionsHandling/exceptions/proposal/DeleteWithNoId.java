package it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.proposal;

public class DeleteWithNoId extends RuntimeException {
    public DeleteWithNoId(String message) {
        super(message);
    }
}