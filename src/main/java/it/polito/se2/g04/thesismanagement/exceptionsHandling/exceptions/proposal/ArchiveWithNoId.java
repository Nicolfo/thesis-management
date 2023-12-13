package it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.proposal;

public class ArchiveWithNoId extends RuntimeException {
    public ArchiveWithNoId(String message) {
        super(message);
    }
}
