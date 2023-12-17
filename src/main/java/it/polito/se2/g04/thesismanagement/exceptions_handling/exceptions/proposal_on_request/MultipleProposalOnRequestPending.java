package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal_on_request;

public class MultipleProposalOnRequestPending extends RuntimeException {
    public MultipleProposalOnRequestPending(String msg) {
        super(msg);
    }
}