package it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.proposal;

public class CreateUpdateProposalWithNoPathVariable extends RuntimeException {
    public CreateUpdateProposalWithNoPathVariable(String message) {
        super(message);
    }
}