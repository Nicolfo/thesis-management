package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.ProposalOnRequest;

public class ProposalRequestWithNoId extends RuntimeException {
    public ProposalRequestWithNoId(String msg) {
        super(msg);
    }
}