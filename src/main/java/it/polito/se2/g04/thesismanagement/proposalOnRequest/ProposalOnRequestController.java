package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.ProposalOnRequest.ProposalRequestWithNoId;
import it.polito.se2.g04.thesismanagement.teacher.Teacher;
import it.polito.se2.g04.thesismanagement.teacher.TeacherDTO;
import it.polito.se2.g04.thesismanagement.teacher.TeacherService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class ProposalOnRequestController {
    private final ProposalOnRequestService proposalOnRequestService;
    private final TeacherService teacherService;

    private final String updateProposalOnRequestWithNoId="you need to insert the id of the proposal";
    @GetMapping("API/proposalOnRequest/getAllPending")
    @PreAuthorize("hasRole('SECRETARY')")
    public List<ProposalOnRequestFullDTO> getAllPendingProposalRequest() {
        return proposalOnRequestService.getAllPending();
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/secretaryAccepted/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('SECRETARY')")
    @ResponseStatus(HttpStatus.OK)
    public ProposalOnRequestDTO updateProposalOnRequestSecretaryAccepted(@PathVariable Long id) throws MessagingException, IOException {
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
        return proposalOnRequestService.proposalOnRequestTeacherChangeStatus(id, ProposalOnRequest.Status.TEACHER_ACCEPTED);
    }
    @PutMapping("/API/proposalOnRequest/updateStatus/teacherChangeRequest/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestteacherChangeRequest(@PathVariable Long id){
        return proposalOnRequestService.proposalOnRequestTeacherChangeStatus(id, ProposalOnRequest.Status.TEACHER_REVIEW);
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
        throw new ProposalRequestWithNoId(updateProposalOnRequestWithNoId);
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/secretaryRejected")
    @PreAuthorize("isAuthenticated() && hasRole('SECRETARY')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestSecretaryRejectedWithNoId(){
        throw new ProposalRequestWithNoId(updateProposalOnRequestWithNoId);
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/teacherAccepted")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherAcceptedWithNoId(){
        throw new ProposalRequestWithNoId(updateProposalOnRequestWithNoId);
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/teacherChangeRequest")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestteacherChangeRequestWithNoId(){
        throw new ProposalRequestWithNoId(updateProposalOnRequestWithNoId);
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/teacherRejected")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherRejectedWithNoId(){
        throw new ProposalRequestWithNoId(updateProposalOnRequestWithNoId);
    }

    @PutMapping("/API/proposalOnRequest/updateStatus/teacherRequestChange/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherRequestChange(@PathVariable Long id){
        return proposalOnRequestService.proposalOnRequestTeacherRequestChange(id);
    }
    @PutMapping("/API/proposalOnRequest/updateStatus/teacherRequestChange")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProposalOnRequestDTO updateProposalOnRequestTeacherRequestChange(){
        throw new ProposalRequestWithNoId(updateProposalOnRequestWithNoId);
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

}
