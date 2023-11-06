package it.polito.se2.g04.thesismanagement.proposal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
public class ProposalController {

    private final ProposalService proposalService;

    public ProposalController(ProposalService proposalService){
        this.proposalService=proposalService;
    }

    @PostMapping("API/proposal/insert/{json}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProposal(@PathVariable String json){
        createProposal(json);
        return;
    }

}
