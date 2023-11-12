package it.polito.se2.g04.thesismanagement.group;

import it.polito.se2.g04.thesismanagement.proposal.ProposalDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalSearchRequest;

import java.util.List;

public interface GroupService {

    /**
     * Get all available groups.
     * @return A list containing all available groups
     */
    List<GroupDTO> getAllGroups();
}
