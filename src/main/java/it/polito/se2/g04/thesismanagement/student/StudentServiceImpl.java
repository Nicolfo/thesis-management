package it.polito.se2.g04.thesismanagement.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService{
    private final StudentRepository studentRepository;
    @Override
    public double getAverageMarks(Long studentId) {
        return studentRepository.getReferenceById(studentId).getGrades().stream().mapToInt(it->it.getGrade()).average().orElse(0);
    }
}
