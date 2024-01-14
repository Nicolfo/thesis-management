package it.polito.se2.g04.thesismanagement.proposal_on_request;

import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal_on_request.ProposalRequestWithNoId;
import it.polito.se2.g04.thesismanagement.teacher.TeacherDTO;
import it.polito.se2.g04.thesismanagement.teacher.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class ProposalOnRequestController {
    private final ProposalOnRequestService proposalOnRequestService;
    private final TeacherService teacherService;

    private static final String UPDATE_PROPOSAL_ON_REQUEST_WITH_NO_ID ="you need to insert the id of the proposal";
    @GetMapping("API/proposalOnRequest/getAllPending")
    @PreAuthorize("hasRole('SECRETARY')")
    public List<ProposalOnRequestFullDTO> getAllPendingProposalRequest() {
        return proposalOnRequestService.getAllPending();
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/secretaryAccepted/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('SECRETARY')")
    @ResponseStatus(HttpStatus.OK)
    public ProposalOnRequestDTO updateProposalOnRequestSecretaryAccepted(@PathVariable Long id) {
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
    @ResponseStatus(HttpStatus.CREATED)
    public ProposalOnRequestDTO createProposalRequest(@RequestBody ProposalOnRequestDTO proposalOnRequestDTO){
        return proposalOnRequestService.createProposalRequest(proposalOnRequestDTO);
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/teacherAccepted/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherAccepted(@PathVariable Long id){
        return proposalOnRequestService.proposalOnRequestTeacherAccepted(id);
    }

    @PutMapping("/API/proposalOnRequest/makeChanges/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('STUDENT')")
    @ResponseStatus(HttpStatus.OK)
    public ProposalOnRequestDTO makeChanges(@PathVariable Long id,@RequestBody ProposalOnRequestDTO proposalOnRequestDTO){
        return proposalOnRequestService.proposalOnRequestMakeChanges(id, proposalOnRequestDTO);
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/teacherRejected/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherRejected(@PathVariable Long id){
        return proposalOnRequestService.proposalOnRequestTeacherChangeStatus(id, ProposalOnRequest.Status.TEACHER_REJECTED);
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/secretaryAccepted")
    @PreAuthorize("isAuthenticated() && hasRole('SECRETARY')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestSecretaryAcceptedWithNoId(){
        throw new ProposalRequestWithNoId(UPDATE_PROPOSAL_ON_REQUEST_WITH_NO_ID);
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/secretaryRejected")
    @PreAuthorize("isAuthenticated() && hasRole('SECRETARY')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestSecretaryRejectedWithNoId(){
        throw new ProposalRequestWithNoId(UPDATE_PROPOSAL_ON_REQUEST_WITH_NO_ID);
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/teacherAccepted")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherAcceptedWithNoId(){
        throw new ProposalRequestWithNoId(UPDATE_PROPOSAL_ON_REQUEST_WITH_NO_ID);
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/teacherRejected")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherRejectedWithNoId(){
        throw new ProposalRequestWithNoId(UPDATE_PROPOSAL_ON_REQUEST_WITH_NO_ID);
    }
    @PutMapping("/API/proposalOnRequest/updateStatus/teacherRequestChange/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherRequestChange(@PathVariable Long id, @RequestBody RequestChangeDTO requestChangeDTO){
        return proposalOnRequestService.proposalOnRequestTeacherRequestChange(id, requestChangeDTO);
    }
    @PutMapping("/API/proposalOnRequest/updateStatus/teacherRequestChange")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherRequestChange(){
        throw new ProposalRequestWithNoId(UPDATE_PROPOSAL_ON_REQUEST_WITH_NO_ID);
    }


    @GetMapping("/API/proposalOnRequest/getByTeacherAccepted")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public List<ProposalOnRequestFullDTO> getPendingRequestsByTeacher() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        TeacherDTO t = teacherService.getByEmail(username);
        return proposalOnRequestService.getPendingRequestsByTeacher(t.getId());
    }

    @GetMapping("/API/proposalOnRequest/getByStudent")
    @PreAuthorize("isAuthenticated() && hasRole('STUDENT')")
    public List<ProposalOnRequestFullDTO> getAllByStudent() {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        return proposalOnRequestService.getProposalOnRequestByStudent(auth.getName());
    }


}
