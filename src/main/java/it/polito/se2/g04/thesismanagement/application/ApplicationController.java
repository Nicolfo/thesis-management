package it.polito.se2.g04.thesismanagement.application;

import it.polito.se2.g04.thesismanagement.security.user.UserInfoUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public  List<ApplicationDTO4> getApplicationByProposal(Long proposalId){
        return applicationService.getApplicationsByProposal(proposalId);
    }

    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('STUDENT')")
    public void insertApplication(@RequestBody ApplicationDTO applicationDTO) {
        applicationService.applyForProposal(applicationDTO);
    }
  
    @GetMapping("/API/application/getApplicationById/{applicationId}")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public  ApplicationDTO4 getApplicationById(@PathVariable Long applicationId){
        return applicationService.getApplicationById(applicationId);
    }

    @GetMapping("/API/application/acceptApplicationById/{applicationId}")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public  boolean acceptApplicationById(@PathVariable Long applicationId){
        return applicationService.acceptApplicationById(applicationId);
    }

    @GetMapping("/API/application/rejectApplicationById/{applicationId}")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public  boolean rejectApplicationById(@PathVariable Long applicationId){
        return applicationService.rejectApplicationById(applicationId);
    }


  /*@PostMapping("/API/application/{applicationId}/accept")
    public void acceptApplication(@PathVariable Long applicationId){
        //TODO:check if the current user is a TEACHER
        applicationService.acceptApplication(applicationId);
    }

    @PostMapping("/API/application/{applicationId}/decline")
    public void declineApplication(@PathVariable Long applicationId){
        //TODO:check if the current user is a TEACHER
        applicationService.declineApplication(applicationId);
    }*/
}
