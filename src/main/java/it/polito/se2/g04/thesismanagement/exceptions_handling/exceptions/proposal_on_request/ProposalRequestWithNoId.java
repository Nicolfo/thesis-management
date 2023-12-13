package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal_on_request;

public class ProposalRequestWithNoId extends RuntimeException {
    public ProposalRequestWithNoId(String msg) {
        super(msg);
    }
}