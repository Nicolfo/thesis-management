package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal_on_request;

public class ProposalInvalidStateException extends RuntimeException {
    public ProposalInvalidStateException(String message){
        super(message);
    }
}
