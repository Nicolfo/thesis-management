package it.polito.se2.g04.thesismanagement.proposal_on_request;

import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;

public interface ProposalOnRequestService {
    public List<ProposalOnRequestFullDTO> getAllPending();
    public ProposalOnRequestDTO proposalOnRequestSecretaryAccepted(Long id) throws MessagingException, IOException;
    public ProposalOnRequestDTO proposalOnRequestSecretaryRejected(Long id);
    public ProposalOnRequestDTO proposalOnRequestTeacherRejected(Long id);
    public ProposalOnRequestDTO createProposalRequest(ProposalOnRequestDTO proposalOnRequestDTO);
    public ProposalOnRequestDTO proposalOnRequestTeacherAccepted(Long id);
    public ProposalOnRequestDTO proposalOnRequestTeacherRequestChange(Long id, RequestChangeDTO requestChangeDTO);
    public ProposalOnRequestDTO proposalOnRequestTeacherChangeStatus(Long id, ProposalOnRequest.Status status);
    public List<ProposalOnRequestFullDTO> getPendingRequestsByTeacher(Long teacherId);

    public List<ProposalOnRequestFullDTO> getProposalOnRequestByStudent(String studentId);

    ProposalOnRequestDTO proposalOnRequestMakeChanges(Long oldId, ProposalOnRequestDTO updatedProposalOnRequestDTO);
}
