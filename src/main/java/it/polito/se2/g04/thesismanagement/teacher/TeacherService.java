package it.polito.se2.g04.thesismanagement.teacher;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface TeacherService {
    List<TeacherDTO> getAll();
    Teacher getById(Long id);
    HashMap<TeacherDTO, String> getAllTeachersGroup();
}
