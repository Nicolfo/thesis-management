package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import it.polito.se2.g04.thesismanagement.proposal.ProposalNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalOnRequestServiceImpl implements ProposalOnRequestService {
    private final ProposalOnRequestRepository proposalOnRequestRepository;

    private final String proposalIsNotPendingError = "Proposal On Request is not pending";

    @Override
    public List<ProposalOnRequestFullDTO> getAllPending() {
        List<ProposalOnRequest> pendingProposals = proposalOnRequestRepository.getProposalOnRequestByStatus(ProposalOnRequest.Status.PENDING);
        List<ProposalOnRequestFullDTO> pendingProposalDTOs = new ArrayList<>();
        for (ProposalOnRequest proposal : pendingProposals) {
            pendingProposalDTOs.add(proposal.toFullDTO());
        }
        return pendingProposalDTOs;
    }

    private ProposalOnRequest checkProposalId(Long id) {
        if (!proposalOnRequestRepository.existsById(id)) {
            String proposalDoNotExistError = "Proposal with this id does not exist";
            throw (new ProposalNotFoundException(proposalDoNotExistError));
        }
        return proposalOnRequestRepository.getReferenceById(id);
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestSecretaryAccepted(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);

        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException(proposalIsNotPendingError));
        }
        proposal.setStatus(ProposalOnRequest.Status.SECRETARY_ACCEPTED);
        proposalOnRequestRepository.save(proposal);

        return proposal.toDTO();
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestSecretaryRejected(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);

        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException(proposalIsNotPendingError));
        }
        proposal.setStatus(ProposalOnRequest.Status.SECRETARY_REJECTED);
        proposalOnRequestRepository.save(proposal);

        return proposal.toDTO();
    }


    @Override
    public ProposalOnRequestDTO proposalOnRequestTeacherAccepted(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException(proposalIsNotPendingError));
        }
        proposal.setStatus(ProposalOnRequest.Status.TEACHER_ACCEPTED);
        proposal.setApprovalDate(new Date());
        proposalOnRequestRepository.save(proposal);
        return proposal.toDTO();
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestTeacherRejected(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException(proposalIsNotPendingError));
        }
        proposal.setStatus(ProposalOnRequest.Status.TEACHER_REJECTED);
        proposalOnRequestRepository.save(proposal);
        return proposal.toDTO();
    }
    @Override
    public ProposalOnRequestDTO proposalOnRequestTeacherRequestChange(Long id) {
        ProposalOnRequest proposal = checkProposalId(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException(proposalIsNotPendingError));
        }
        proposal.setStatus(ProposalOnRequest.Status.TEACHER_REVIEW);
        proposalOnRequestRepository.save(proposal);
        return proposal.toDTO();
    }

}


