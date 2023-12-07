package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import it.polito.se2.g04.thesismanagement.proposal.ProposalService;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class ProposalOnRequestController {
    private final ProposalOnRequestService proposalOnRequestService;
    @GetMapping("API/proposalOnRequest/getAllPending")
    @PreAuthorize("hasRole('SECRETARY')")
    public List<ProposalOnRequestDTO> getAllPendingProposalRequest() {
        return proposalOnRequestService.getAllPending();
    }

}
