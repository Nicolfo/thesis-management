package it.polito.se2.g04.thesismanagement.career;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CareerService {
    /**
     * @param studentId Long describing the id of the Student of who the grades should be returned
     * @return List of Career, describing the grades the student with the given studentId has archieved
     */
    public List<CareerDTO> getGradesForStudent (Long studentId);
}
