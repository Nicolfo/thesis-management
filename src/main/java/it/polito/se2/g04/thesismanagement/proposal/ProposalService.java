package it.polito.se2.g04.thesismanagement.proposal;

public interface ProposalService {
    Proposal createProposal(String jsonProposal);
    Proposal updateProposal(Long id, String jsonProposal);
}
