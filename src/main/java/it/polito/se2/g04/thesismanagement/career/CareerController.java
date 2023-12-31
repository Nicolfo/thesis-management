package it.polito.se2.g04.thesismanagement.career;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;

    /**
     * @param studentId Long describing the id of the Student of who the grades should be returned
     * @return List of Career, describing the grades the student with the given studentId has archieved
     */
    @GetMapping("/API/career/getByStudent/{studentId}")
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    @ResponseStatus(HttpStatus.OK)
    public List<CareerDTO> getGradesForStudent(@PathVariable Long studentId){
        return careerService.getGradesForStudent(studentId);
    }

}
