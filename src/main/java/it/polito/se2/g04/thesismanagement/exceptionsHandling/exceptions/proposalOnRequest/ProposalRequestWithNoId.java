package it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.proposalOnRequest;

public class ProposalRequestWithNoId extends RuntimeException {
    public ProposalRequestWithNoId(String msg) {
        super(msg);
    }
}