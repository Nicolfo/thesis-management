package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import java.util.List;

public interface ProposalOnRequestService {
    public List<ProposalOnRequestFullDTO> getAllPending();
    public ProposalOnRequestDTO proposalOnRequestSecretaryAccepted(Long id);
    public ProposalOnRequestDTO proposalOnRequestSecretaryRejected(Long id);
    public ProposalOnRequestDTO proposalOnRequestTeacherRejected(Long id);

    ProposalOnRequestDTO createProposalRequest(ProposalOnRequestDTO proposalOnRequestDTO);

    public ProposalOnRequestDTO proposalOnRequestTeacherAccepted(Long id);
    public ProposalOnRequestDTO proposalOnRequestTeacherRequestChange(Long id);

}
