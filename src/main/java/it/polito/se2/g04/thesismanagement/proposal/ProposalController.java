package it.polito.se2.g04.thesismanagement.proposal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class ProposalController {

    private final ProposalService proposalService;

    public ProposalController(ProposalService proposalService) {
        this.proposalService = proposalService;
    }

    @GetMapping("/API/proposal/getAll")
    @ResponseStatus(HttpStatus.OK)
    public List<Proposal> getAllProposals() {
        return proposalService.getAllProposals();
    }

    @GetMapping("/API/proposal/getByProf")
    @ResponseStatus(HttpStatus.OK)
    public List<Proposal> getProposalsByProf() {
        return proposalService.getProposalsByProf(1);//TODO change ID
    }
}
