package it.polito.se2.g04.thesismanagement.student;

import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    @GetMapping("/API/student/getAverageMarks/{studentId}")
    public double getAverageMarks(@PathVariable Long studentId) {
        return studentService.getAverageMarks(studentId);
    }

    @GetMapping("/API/application/getNameById")
    public String getStudentFullName(Long studentId) {
        return studentService.getStudentFullName(studentId);
    }
}



