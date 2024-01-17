package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal;

public class GetProposalWithNoId extends RuntimeException{
    public GetProposalWithNoId(String message) {
        super(message);
    }
}
