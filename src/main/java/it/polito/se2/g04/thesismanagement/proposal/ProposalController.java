package it.polito.se2.g04.thesismanagement.proposal;

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

    public ProposalController(ProposalService proposalService) {
        this.proposalService = proposalService;
    }

    /**
     * @return List<Proposal> List of all not archived proposals
     */
    @GetMapping("/API/proposal/getAll")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public List<ProposalFullDTO> getAllProposals() {
        return proposalService.getAllNotArchivedProposals();
    }

    /**
     * @return List<Proposal> list of all not archived proposals, that have the currently logged in teacher as
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
    @ResponseStatus(HttpStatus.CREATED)
    public void UpdateProposal(@PathVariable Long id, @RequestBody ProposalDTO proposal){
        proposalService.updateProposal(id, proposal);

    }

    @PostMapping("/API/proposal/update/")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void UpdateProposalWithNoPathVariable(){
        throw new createUpdateProposalWithNoPathVariable("Can't update a proposal without filling the form");
    }

    @PostMapping("/API/proposal/search")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public List<ProposalFullDTO> searchProposals(@RequestBody ProposalSearchRequest proposalSearchRequest) {
        return proposalService.searchProposals(proposalSearchRequest);
    }

}
