package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Proposal;

public class ProposalNotFoundException extends RuntimeException {
    public ProposalNotFoundException(String message) {
        super(message);
    }
}
