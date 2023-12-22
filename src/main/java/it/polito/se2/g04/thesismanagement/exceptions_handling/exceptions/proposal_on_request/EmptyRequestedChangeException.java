package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal_on_request;

public class EmptyRequestedChangeException extends RuntimeException {
    public EmptyRequestedChangeException(String msg) {
        super(msg);
    }
}
