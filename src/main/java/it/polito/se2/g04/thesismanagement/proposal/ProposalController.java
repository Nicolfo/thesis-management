package it.polito.se2.g04.thesismanagement.proposal;


import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal.ArchiveWithNoId;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal.CreateUpdateProposalWithNoPathVariable;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal.DeleteWithNoId;
import it.polito.se2.g04.thesismanagement.student.StudentService;
import it.polito.se2.g04.thesismanagement.teacher.TeacherService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
public class ProposalController {

    private final ProposalService proposalService;

    private final StudentService studentService;

    private final TeacherService teacherService;

    public ProposalController(ProposalService proposalService, StudentService studentService, TeacherService teacherService) {
        this.proposalService = proposalService;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    /**
     * @return List<ProposalFullDTO> List of all not archived proposals
     */
    @GetMapping("/API/proposal/getAll")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public List<ProposalFullDTO> getAllProposals() {
        return proposalService.getAllNotArchivedProposals();
    }

    /**
     * @return List<ProposalFullDTO> list of all not archived proposals, that have the currently logged in teacher as
     * supervisor or co-supervisor. If there are no proposals for that teacher or the logged-in user is not a teacher,
     * an empty List is returned
     */
    @GetMapping("/API/proposal/getByProf")
    @ResponseStatus(HttpStatus.OK)
    public List<ProposalFullDTO> getProposalsByProf() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return proposalService.getProposalsByProf(username);
    }




    @GetMapping("/API/proposal/getTitleByProposalId/{proposalId}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public String getTitleByProposalId(@PathVariable Long proposalId) {
        return proposalService.getTitleByProposalId(proposalId);
    }



    @PostMapping("/API/proposal/insert/")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProposal(@RequestBody ProposalDTO proposal){
        proposalService.createProposal(proposal);
    }



    @PutMapping("/API/proposal/update/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public void updateProposal(@PathVariable Long id, @RequestBody ProposalDTO proposal){
        proposalService.updateProposal(id, proposal);

    }

    @PutMapping("/API/proposal/update")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void updateProposalWithNoPathVariable(){
        throw new CreateUpdateProposalWithNoPathVariable("Can't update a proposal without filling the form");
    }

    @PostMapping("/API/proposal/search")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public List<ProposalFullDTO> searchProposals(@RequestBody ProposalSearchRequest proposalSearchRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String cds = studentService.getCdS(username);
        proposalSearchRequest.setCds(cds);
        return proposalService.searchProposals(proposalSearchRequest);
    }

    @PostMapping("/API/proposal/searchArchived")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public List<ProposalFullDTO> searchArchivedProposals(@RequestBody ProposalSearchRequest proposalSearchRequest) {
        // Automatically extract the id of the logged in teacher, and filter the
        // archived proposals by it.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Long supervisorId = teacherService.getByEmail(username).getId();
        proposalSearchRequest.setSupervisorIdList(List.of(supervisorId));
        return proposalService.searchArchivedProposals(proposalSearchRequest);
    }

    @GetMapping("/API/proposal/getArchived")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public List<ProposalFullDTO> searchArchivedProposals() {
        // Automatically extract the id of the logged in teacher, and filter the
        // archived proposals by it.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return proposalService.getArchivedProposals(username);
    }

    @PostMapping("/API/proposal/archive/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public void archiveProposal(@PathVariable Long id){
        proposalService.archiveProposal(id);
    }

    @DeleteMapping("/API/proposal/delete/{id}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public String deleteProposal(@PathVariable Long id){
        proposalService.deleteProposal(id);
        return "Deleted correctly";
    }

    @DeleteMapping("/API/proposal/delete")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProposalWithNoId(){
        throw new DeleteWithNoId("can't delete a proposal without his id");
    }

    @PostMapping("/API/proposal/archive")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public void archiveProposalWithNoId(){
        throw new ArchiveWithNoId("can't archive a proposal without his id");
    }

    @GetMapping("/API/proposal/getAllProposalByCoSupervisor/{email}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public List<ProposalFullDTO> getAllProposalByCoSupervisor(@PathVariable String email){
        return proposalService.getAllProposalByCoSupervisor(email);
    }

    @GetMapping("/API/proposal/getAllProposalByCoSupervisor")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public void getAllProposalByCoSupervisorWithNoId(){
        //TODO error handler
    }
}
