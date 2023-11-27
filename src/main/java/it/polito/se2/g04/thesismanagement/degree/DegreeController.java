package it.polito.se2.g04.thesismanagement.degree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class DegreeController {

    @Autowired
    private final DegreeService degreeService;

    public DegreeController(DegreeService degreeService){
        this.degreeService=degreeService;
    }

    @GetMapping("/API/Degree/getAllNames")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public List<String> getAllNames(){
        return degreeService.getAllName();
    }
}
