package it.polito.se2.g04.thesismanagement.student;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    @GetMapping("/API/student/getAverageMarks/{studentId}")
    public double getAverage(@PathVariable Long studentId){
        return studentService.getAverageMarks(studentId);
    }

}
