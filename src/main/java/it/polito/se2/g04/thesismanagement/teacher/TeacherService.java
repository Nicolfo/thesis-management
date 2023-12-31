package it.polito.se2.g04.thesismanagement.teacher;


import java.util.List;

public interface TeacherService {
    List<TeacherDTO> getAll();
    TeacherDTO getById(Long id);
    TeacherDTO getByEmail(String email);
}
