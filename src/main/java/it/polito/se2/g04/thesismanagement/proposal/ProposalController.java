package it.polito.se2.g04.thesismanagement.proposal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import it.polito.se2.g04.thesismanagement.security.user.User;
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
    @PreAuthorize("permitAll()")
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
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        return proposalService.getProposalsByProf(username);
    }
}
