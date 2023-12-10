package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/API/proposalOnRequest/updateStatus/teacherAccepted/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherAccepted(@PathVariable Long id){
        return proposalOnRequestService.proposalOnRequestTeacherAccepted(id);

    }

    @PutMapping("/API/proposalOnRequest/updateStatus/teacherRejected/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherRejected(@PathVariable Long id){
        return proposalOnRequestService.proposalOnRequestTeacherRejected(id);

    }

    @PutMapping("/API/proposalOnRequest/updateStatus/secretaryAccepted")
    @PreAuthorize("isAuthenticated() && hasRole('SECRETARY')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestSecretaryAcceptedWithNoId(){
        throw new proposalRequestWithNoId("you need to insert the id of the proposal");
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/secretaryRejected")
    @PreAuthorize("isAuthenticated() && hasRole('SECRETARY')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestSecretaryRejectedWithNoId(){
        throw new proposalRequestWithNoId("you need to insert the id of the proposal");
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/teacherAccepted")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherAcceptedWithNoId(){
        throw new proposalRequestWithNoId("you need to insert the id of the proposal");
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/teacherRejected")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherRejectedWithNoId(){
        throw new proposalRequestWithNoId("you need to insert the id of the proposal");
    }

}
