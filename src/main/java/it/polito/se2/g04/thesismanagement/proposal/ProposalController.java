package it.polito.se2.g04.thesismanagement.proposal;

import it.polito.se2.g04.thesismanagement.security.user.UserInfoUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import it.polito.se2.g04.thesismanagement.security.user.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@CrossOrigin
public class ProposalController {

    private final ProposalService proposalService;

    public ProposalController(ProposalService proposalService) {
        this.proposalService = proposalService;
    }

    /**
     * @return List<Proposal> List of all not archived proposals
     */
    @GetMapping("/API/proposal/getAll")
    //@PreAuthorize("hasAuthority('STUDENT')")
    @ResponseStatus(HttpStatus.OK)
    public List<Proposal> getAllProposals() {
        return proposalService.getAllNotArchivedProposals();
    }

    /**
     * @return List<Proposal> list of all not archived proposals, that have the currently logged in teacher as
     * supervisor or co-supervisor. If there are no proposals for that teacher or the logged-in user is not a teacher,
     * an empty List is returned
     */
    @GetMapping("/API/proposal/getByProf")
    @ResponseStatus(HttpStatus.OK)
    public List<Proposal> getProposalsByProf() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfoUserDetails user = (UserInfoUserDetails) auth.getPrincipal();
        String username = user.getUsername();
        return proposalService.getProposalsByProf(username);
    }

    @GetMapping("/API/proposal/getTitleByProposalId/{proposalId}")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public String getTitleByProposalId(@PathVariable Long proposalId) {
        return proposalService.getTitleByProposalId(proposalId);
    }



    @PostMapping("API/proposal/insert/{json}")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Proposal createProposal(@RequestBody ProposalDTO proposal){
        return proposalService.createProposal(proposal);
    }



    @PutMapping("API/proposal/update/{id}")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Proposal UpdateProposal(@PathVariable Long id, @RequestBody ProposalDTO proposal){
        return proposalService.updateProposal(id, proposal);
    }

    @PostMapping("API/proposal/update/")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void UpdateProposalWithNoPathVariable(){
        throw new createUpdateProposalWithNoPathVariable("Can't update a proposal without filling the form");
    }

    @PostMapping("/API/proposal/search")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public List<Proposal> searchProposals(@RequestBody ProposalSearchRequest proposalSearchRequest) {
        return proposalService.searchProposals(proposalSearchRequest);
    }

}
