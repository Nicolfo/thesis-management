package it.polito.se2.g04.thesismanagement.proposal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Proposal createProposal(@PathVariable String json){
        return proposalService.createProposal(json);
    }

    @PostMapping("API/proposal/insert/")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void createProposalWithNoPathVariable(){
        throw new createUpdateProposalWithNoPathVariable("Can't create a proposal without filling the form");
    }

    @PutMapping("API/proposal/update/{id}")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Proposal UpdateProposal(@PathVariable Long id, @RequestBody String json){
        return proposalService.updateProposal(id, json);
    }

    @PostMapping("API/proposal/update/")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void UpdateProposalWithNoPathVariable(){
        throw new createUpdateProposalWithNoPathVariable("Can't update a proposal without filling the form");
    }

}
