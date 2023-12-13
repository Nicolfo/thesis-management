package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal;

public class CreateUpdateProposalWithNoPathVariable extends RuntimeException {
    public CreateUpdateProposalWithNoPathVariable(String message) {
        super(message);
    }
}