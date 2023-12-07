package it.polito.se2.g04.thesismanagement.proposalOnRequest;

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
}
