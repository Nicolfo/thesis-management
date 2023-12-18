package it.polito.se2.g04.thesismanagement.proposal_on_request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProposalOnRequestRepository extends JpaRepository<ProposalOnRequest,Long> {
    public List<ProposalOnRequest> getProposalOnRequestByStatus(ProposalOnRequest.Status status);
    public boolean existsProposalOnRequestByStudentIdAndStatusNotIn(Long studentId, Collection<ProposalOnRequest.Status> status);
    public List<ProposalOnRequest> getProposalOnRequestsBySupervisorIdAndStatus(Long id,ProposalOnRequest.Status status);

    public List<ProposalOnRequest> getProposalOnRequestsByStudentEmail(String email);

}
