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
        applicationService.applyForProposal(applicationDTO);
    }


}
