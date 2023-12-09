package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import it.polito.se2.g04.thesismanagement.application.Application;
import it.polito.se2.g04.thesismanagement.application.ApplicationStatus;
import it.polito.se2.g04.thesismanagement.proposal.Proposal;
import it.polito.se2.g04.thesismanagement.proposal.ProposalFullDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ProposalOnRequestServiceImpl implements ProposalOnRequestService{
    private final ProposalOnRequestRepository proposalOnRequestRepository;
    @Override
    public List<ProposalOnRequestDTO> getAllPending() {
        return proposalOnRequestRepository.getProposalOnRequestByStatus(ProposalOnRequest.Status.PENDING).stream().map(it->it.toDTO()).toList();
    }

    @Override
     public ProposalOnRequestDTO proposalOnRequestSecretaryAccepted(Long id){
        if (!proposalOnRequestRepository.existsById(id)) {
            throw (new ProposalNotFoundException("Proposal with this id does not exist"));
        }

        ProposalOnRequest proposal = proposalOnRequestRepository.getReferenceById(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException("Proposal On Request is not pending"));
        }
        proposal.setStatus(ProposalOnRequest.Status.SECRETARY_ACCEPTED);
        proposalOnRequestRepository.save(proposal);

        return proposal.toDTO();
    }

    @Override
    public ProposalOnRequestDTO proposalOnRequestSecretaryRejected(Long id){
        if (!proposalOnRequestRepository.existsById(id)) {
            throw (new ProposalNotFoundException("Proposal with this id does not exist"));
        }

        ProposalOnRequest proposal = proposalOnRequestRepository.getReferenceById(id);
        if (proposal.getStatus() != ProposalOnRequest.Status.PENDING) {
            throw (new ProposalNotFoundException("Proposal On Request is not pending"));
        }
        proposal.setStatus(ProposalOnRequest.Status.SECRETARY_REJECTED);
        proposalOnRequestRepository.save(proposal);

        return proposal.toDTO();
    }





}


