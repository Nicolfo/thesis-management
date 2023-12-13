package it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.proposal;

public class ProposalNotFoundException extends RuntimeException {
    public ProposalNotFoundException(String message) {
        super(message);
    }
}
