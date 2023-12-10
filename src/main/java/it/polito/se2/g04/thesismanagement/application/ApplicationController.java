package it.polito.se2.g04.thesismanagement.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    private static String parameterMissingError="Id must be one of the given parameter";

    @GetMapping("/API/application/getByProf")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public List<ApplicationDTO> getAllByProf() {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        return applicationService.getApplicationsByProf(auth.getName());
    }

    @GetMapping("/API/application/getByStudent")
    @PreAuthorize("isAuthenticated() && hasRole('STUDENT')")
    public List<ApplicationDTO> getAllByStudent() {
        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        return applicationService.getApplicationsByStudent(auth.getName());
    }

    @GetMapping("/API/application/getApplicationsByProposal")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public List<ApplicationDTO> getApplicationByProposal(Long proposalId) {
        return applicationService.getApplicationsByProposal(proposalId);
    }

    @GetMapping("/API/application/getApplicationsByProposalId/{proposalId}")
    @PreAuthorize("isAuthenticated()")
    public List<ApplicationDTO> getApplicationByProposalId(@PathVariable Long proposalId) {
        return applicationService.getApplicationsByProposalId(proposalId);
    }

    @PostMapping("/API/application/insert/")
    @PreAuthorize("isAuthenticated() && hasRole('STUDENT')")
    public void insertApplication(@RequestBody ApplicationDTO applicationDTO) {
        applicationService.applyForProposal(applicationDTO);
    }

    @GetMapping("/API/application/getApplicationById/{applicationId}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public ApplicationDTO getApplicationById(@PathVariable Long applicationId) {
        return applicationService.getApplicationById(applicationId);
    }

    /**
     * This method changes the state of the Application with the passed id to "ACCEPTED". This is only fulfilled,when
     * the current state is "PENDING". Otherwise, (or if an error occurs), the state is not changed and false is returned
     *
     * @param applicationId id of application to be accepted
     * @return true if accepting was successful, false if accepting was not successful
     */
    @GetMapping("/API/application/acceptApplicationById/{applicationId}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public boolean acceptApplicationById(@PathVariable Long applicationId) {
        return applicationService.acceptApplicationById(applicationId);
    }

    /**
     * This method changes the state of the Application with the passed id to "REJECTED". This is only fulfilled,when
     * the current state is "PENDING". Otherwise, (or if an error occurs), the state is not changed and false is returned
     *
     * @param applicationId id of application to be rejected
     * @return true if rejection was successful, false if rejection was not successful
     */
    @GetMapping("/API/application/rejectApplicationById/{applicationId}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public boolean rejectApplicationById(@PathVariable Long applicationId) {
        return applicationService.rejectApplicationById(applicationId);
    }


    /**
     * This method changes the state of the Application with the passed id to the passed state. This is done regardless
     * the current state of the application. If the change was succesfull, true ist returned. If an error occurs, false
     * is returned.
     *
     * @param applicationId id of application of which the state should be changed
     * @param newState String describing the state, to which the application should be changed to
     * @return true if changing the state was successful, false if changing the state was not successful
     */
    @GetMapping("/API/application/changeApplicationStateById/{applicationId}/{newState}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public boolean changeApplicationStateById(@PathVariable Long applicationId, @PathVariable String newState) {
        return applicationService.changeApplicationStateById(applicationId, newState);
    }

    @GetMapping("/API/application/rejectApplicationById/")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public boolean rejectApplicationByIdWithoutId() {
        throw new ApplicationBadRequestFormatException(parameterMissingError);
    }

    @GetMapping("/API/application/acceptApplicationById/")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public boolean acceptApplicationByIdWithoutId() {
        throw new ApplicationBadRequestFormatException(parameterMissingError);
    }

    @GetMapping("/API/application/getApplicationById/")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public boolean getApplicationByIdWithoutId() {
        throw new ApplicationBadRequestFormatException(parameterMissingError);
    }
}
