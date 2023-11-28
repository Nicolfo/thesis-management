package it.polito.se2.g04.thesismanagement.teacher;

import it.polito.se2.g04.thesismanagement.group.GroupDTO;

import java.util.List;

public interface TeacherService {
    List<TeacherDTO> getAllTeachers();
    List<TeacherDTO> getAll();
    TeacherDTO getById(Long id);
    TeacherDTO getByEmail(String email);
}
