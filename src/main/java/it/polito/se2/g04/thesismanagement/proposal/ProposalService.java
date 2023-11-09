package it.polito.se2.g04.thesismanagement.proposal;

import java.util.List;

public interface ProposalService {

    List<Proposal> getAllProposals();
    List<Proposal> getProposalsByProf(long teacherId);
}

