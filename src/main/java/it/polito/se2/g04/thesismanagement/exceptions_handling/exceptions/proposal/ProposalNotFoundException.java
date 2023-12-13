package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal;

public class ProposalNotFoundException extends RuntimeException {
    public ProposalNotFoundException(String message) {
        super(message);
    }
}
