package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal;

public class getProposalWithNoId extends RuntimeException{
    public getProposalWithNoId(String message) {
        super(message);
    }
}
