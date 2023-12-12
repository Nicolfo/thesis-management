package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Proposal;

public class CreateUpdateProposalWithNoPathVariable extends RuntimeException {
    public CreateUpdateProposalWithNoPathVariable(String message) {
        super(message);
    }
}