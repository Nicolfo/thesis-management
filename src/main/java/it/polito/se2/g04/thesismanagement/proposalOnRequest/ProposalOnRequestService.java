package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import java.util.List;

public interface ProposalOnRequestService {
    public List<ProposalOnRequestDTO> getAllPending();
    public ProposalOnRequestDTO proposalOnRequestSecretaryAccepted(Long id);
    public ProposalOnRequestDTO proposalOnRequestSecretaryRejected(Long id);
    public ProposalOnRequestDTO proposalOnRequestTeacherRejected(Long id);
    public ProposalOnRequestDTO proposalOnRequestTeacherAccepted(Long id);
    public ProposalOnRequestDTO proposalOnRequestTeacherRequestChange(Long id);

}
