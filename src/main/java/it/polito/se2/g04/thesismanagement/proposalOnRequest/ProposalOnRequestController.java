package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import it.polito.se2.g04.thesismanagement.proposal.ProposalDTO;
import it.polito.se2.g04.thesismanagement.proposal.ProposalService;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.annotations.Body;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/API/proposalOnRequest/updateStatus/secretaryAccepted/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('SECRETARY')")
    @ResponseStatus(HttpStatus.OK)
    public ProposalOnRequestDTO updateProposalOnRequestSecretaryAccepted(@PathVariable Long id){
       return proposalOnRequestService.proposalOnRequestSecretaryAccepted(id);

    }

    @PutMapping("/API/proposalOnRequest/updateStatus/secretaryRejected/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('SECRETARY')")
    @ResponseStatus(HttpStatus.OK)
    public ProposalOnRequestDTO updateProposalOnRequestSecretaryRejected(@PathVariable Long id){
        return proposalOnRequestService.proposalOnRequestSecretaryRejected(id);

    }
    @PostMapping("/API/proposalOnRequest/create/")
    @PreAuthorize("isAuthenticated() && hasRole('STUDENT')")
    public ProposalOnRequestDTO createProposalRequest(@RequestBody ProposalOnRequestDTO proposalOnRequestDTO){
        return proposalOnRequestService.createProposalRequest(proposalOnRequestDTO);
    }

}
