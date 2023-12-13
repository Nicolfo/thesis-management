package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProposalOnRequestRepository extends JpaRepository<ProposalOnRequest,Long> {
    public List<ProposalOnRequest> getProposalOnRequestByStatus(ProposalOnRequest.Status status);
    public List<ProposalOnRequest> getProposalOnRequestsBySupervisorIdAndStatus(Long id,ProposalOnRequest.Status status);

    public List<ProposalOnRequest> getProposalOnRequestsByStudentEmail(String email);

}
