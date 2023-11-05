package it.polito.se2.g04.thesismanagement.application;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin
public class ApplicationController {
    @GetMapping("/API/application/getByStudent")
    public List<Application> getAll(){
        return Collections.emptyList();
    }
}
