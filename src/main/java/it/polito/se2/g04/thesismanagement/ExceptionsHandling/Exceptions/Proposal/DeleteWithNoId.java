package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Proposal;

public class DeleteWithNoId extends RuntimeException {
    public DeleteWithNoId(String message) {
        super(message);
    }
}