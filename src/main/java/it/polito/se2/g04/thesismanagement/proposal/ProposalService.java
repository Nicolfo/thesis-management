package it.polito.se2.g04.thesismanagement.proposal;

import java.util.List;

public interface ProposalService {

    /**
     * @return List<Proposal> all proposals, including archived and not archived ones.
     */
    List<ProposalFullDTO> getAllProposals();

    /**
     *
     * @return List<Proposal> all proposals that are not archived
     */
    List<ProposalFullDTO> getAllNotArchivedProposals();

    /**
     * @param userName String the username of the user of which the proposals should be returned. Should be equal
     *                 to the email address in Teacher table
     * @return List<Proposal> all Proposals that have the passed teacher (found by Username) as supervisor or supervisor.
     * archived proposals are not considered. If no proposals where found or the passed User is not a Teacher, an empty
     * List is returned
     */
    List<ProposalFullDTO> getProposalsByProf(String userName);

    String getTitleByProposalId(Long proposalId);
    ProposalFullDTO createProposal(ProposalDTO proposalDTO);
    ProposalFullDTO updateProposal(Long id, ProposalDTO proposal);
    /**
     * A search method that allows to filter active proposals.
     * @param proposalSearchRequest request object containing all filters
     * @return A list of ProposalDTO objects representing the search's results.
     */
    List<ProposalFullDTO> searchProposals(ProposalSearchRequest proposalSearchRequest);

    /**
     * A search method that allows to filter archived proposals.
     * @param proposalSearchRequest request object containing all filters
     * @return A list of ProposalDTO objects representing the search's results.
     */
    List <ProposalFullDTO> searchArchivedProposals(ProposalSearchRequest proposalSearchRequest);
    List <ProposalFullDTO> getArchivedProposals(String userName);


    ProposalFullDTO archiveProposal(Long id);
    void deleteProposal(Long id);

}

