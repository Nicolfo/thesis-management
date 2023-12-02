package it.polito.se2.g04.thesismanagement.proposal;


import it.polito.se2.g04.thesismanagement.student.StudentService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@CrossOrigin
public class ProposalController {

    private final ProposalService proposalService;

    private final StudentService studentService;

    public ProposalController(ProposalService proposalService, StudentService studentService) {
        this.proposalService = proposalService;
        this.studentService = studentService;
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
    public void createProposal(@RequestBody ProposalDTO proposal, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw (new createUpdateProposalWithNoPathVariable(
                    String.join(bindingResult.getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toString()))
            );
        }
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
        throw new createUpdateProposalWithNoPathVariable("Can't update a proposal without filling the form");
    }

    @PostMapping("/API/proposal/search")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public List<ProposalFullDTO> searchProposals(@RequestBody ProposalSearchRequest proposalSearchRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String cds = studentService.getCdS(username);
        proposalSearchRequest.setCdS(cds);
        return proposalService.searchProposals(proposalSearchRequest);
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
        throw new deleteWithNoId("can't delete a proposal without his id");
    }

    @PostMapping("/API/proposal/archive")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public void archiveProposalWithNoId(){
        throw new archiveWithNoId("can't archive a proposal without his id");
    }

}
