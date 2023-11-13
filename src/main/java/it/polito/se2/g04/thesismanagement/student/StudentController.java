package it.polito.se2.g04.thesismanagement.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@CrossOrigin
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;
  
    @GetMapping("/API/student/getAverageMarks/{studentId}")
    public double getAverage(@PathVariable Long studentId){
        return studentService.getAverageMarks(studentId);
    }

    @GetMapping("/API/application/getNameById")
    public StudentDTO getStudentInfo(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        return new StudentDTO(student.getName() + " " + student.getSurname());
    }



