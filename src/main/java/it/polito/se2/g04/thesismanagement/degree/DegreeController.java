package it.polito.se2.g04.thesismanagement.degree;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class DegreeController {

    private final DegreeService degreeService;

    @GetMapping("/API/Degree/getAllNames")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public List<String> getAllNames(){
        return degreeService.getAllName();
    }
}
