package it.polito.se2.g04.thesismanagement.proposal_on_request;


import java.util.List;

public interface ProposalOnRequestService {
    List<ProposalOnRequestFullDTO> getAllPending();
    ProposalOnRequestDTO proposalOnRequestSecretaryAccepted(Long id);
    ProposalOnRequestDTO proposalOnRequestSecretaryRejected(Long id);
    ProposalOnRequestDTO proposalOnRequestTeacherRejected(Long id);
    ProposalOnRequestDTO createProposalRequest(ProposalOnRequestDTO proposalOnRequestDTO);
    ProposalOnRequestDTO proposalOnRequestTeacherAccepted(Long id);
    ProposalOnRequestDTO proposalOnRequestTeacherRequestChange(Long id, RequestChangeDTO requestChangeDTO);
    ProposalOnRequestDTO proposalOnRequestTeacherChangeStatus(Long id, ProposalOnRequest.Status status);
    List<ProposalOnRequestFullDTO> getPendingRequestsByTeacher(Long teacherId);

    public List<ProposalOnRequestFullDTO> getProposalOnRequestByStudent(String studentId);

    ProposalOnRequestDTO proposalOnRequestMakeChanges(Long oldId, ProposalOnRequestDTO updatedProposalOnRequestDTO);
}
