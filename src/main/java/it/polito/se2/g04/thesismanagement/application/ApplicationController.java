package it.polito.se2.g04.thesismanagement.application;

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
    @GetMapping("/API/application/getByStudent")
    public List<Application> getAll(){
        return Collections.emptyList();
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
