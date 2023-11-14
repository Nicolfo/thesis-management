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

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @GetMapping("/API/application/getByProf")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public List<ApplicationDTO2> getAllByProf(){
        UserInfoUserDetails auth = (UserInfoUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return applicationService.getApplicationsByProf(auth.getUsername());
    }

    @GetMapping("/API/application/getByStudent")
    @PreAuthorize("isAuthenticated() && hasAuthority('STUDENT')")
    public List<ApplicationDTO3> getAllByStudent(){
        UserInfoUserDetails auth = (UserInfoUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return applicationService.getApplicationsByStudent(auth.getUsername());
    }

    @GetMapping("/API/application/getApplicationsByProposal")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public  List<Application> getApplicationByProposal(Long proposalId){
        return applicationService.getApplicationsByProposal(proposalId);
    }


    @PostMapping("/API/application/insert")
    @ResponseStatus(HttpStatus.CREATED)
    public void insertApplication(@RequestBody ApplicationDTO applicationDTO){
        //TODO:check if the current user is a STUDENT
        applicationService.applyForProposal(applicationDTO);
    }

    @PostMapping("/API/application/{applicationId}/accept")
    public void acceptApplication(@PathVariable Long applicationId){
        //TODO:check if the current user is a TEACHER
        applicationService.acceptApplication(applicationId);
    }

    @PostMapping("/API/application/{applicationId}/decline")
    public void declineApplication(@PathVariable Long applicationId){
        //TODO:check if the current user is a TEACHER
        applicationService.declineApplication(applicationId);
    }
}
