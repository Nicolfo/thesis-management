package it.polito.se2.g04.thesismanagement.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public String getStudentFullName(Long studentId) {
        // Fetch the student entity from the database
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        // Concatenate name and surname and return as a single string
        return student.getName() + " " + student.getSurname();
    }
}
