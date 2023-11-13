package it.polito.se2.g04.thesismanagement.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService{
    private final StudentRepository studentRepository;

    @Override
    public String getStudentFullName(Long studentId) {
        // Fetch the student entity from the database
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        // Concatenate name and surname and return as a single string
        return student.getName() + " " + student.getSurname();
    }

    @Override
    public double getAverageMarks(Long studentId) {
        return studentRepository.getReferenceById(studentId).getGrades().stream().mapToInt(it->it.getGrade()).average().orElse(0);
    }

    public StudentDTO getStudentInfo(Long id){
       Student student= studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return new StudentDTO(student.getName() + " " + student.getSurname());
    }
}
