package it.polito.se2.g04.thesismanagement.career;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerServiceImpl implements CareerService {
    private final CareerRepository careerRepository;


    @Override
    public List<CareerDTO> getGradesForStudent(Long studentId) {
        return careerRepository.getAllByStudentId(studentId).stream().map(CareerDTO::fromCareer).toList();
    }
}
