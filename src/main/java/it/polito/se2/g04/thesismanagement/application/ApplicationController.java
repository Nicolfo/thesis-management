package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.proposal.ProposalRepository;
import it.polito.se2.g04.thesismanagement.security.user.User;
import it.polito.se2.g04.thesismanagement.security.user.UserInfoUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    @GetMapping("/API/application/getByStudent")
    public List<Application> getAll(){
        return Collections.emptyList();
    }

    @GetMapping("/API/application/getByProf")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public List<Application> getAllByProf(){
        UserInfoUserDetails auth = (UserInfoUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return applicationService.getApplicationsByProf(auth.getUsername());

    }

    @GetMapping("/API/applicatio/getApplicationsByProposal")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public  List<Application> getApplicationByProposal(Long proposalId){
        return applicationService.getApplicationsByProposal(proposalId);
    }

}
