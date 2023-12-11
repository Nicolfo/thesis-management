package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Proposal;

public class ArchiveWithNoId extends RuntimeException {
    public ArchiveWithNoId(String message) {
        super(message);
    }
}
