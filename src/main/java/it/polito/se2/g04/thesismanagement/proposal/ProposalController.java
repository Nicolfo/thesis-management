package it.polito.se2.g04.thesismanagement.proposal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
public class ProposalController {

    @Autowired
    private final ProposalService proposalService;

    public ProposalController(ProposalService proposalService){
        this.proposalService=proposalService;
    }

    @PostMapping("API/proposal/insert/{json}")
    @ResponseStatus(HttpStatus.CREATED)
    public Proposal createProposal(@PathVariable String json){
        return proposalService.createProposal(json);
    }

    @PostMapping("API/proposal/insert/")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void createProposalWithNoPathVariable(){
        throw new createProposalWithNoPathVariable("Can't create a proposal without filling the form");
    }
}
