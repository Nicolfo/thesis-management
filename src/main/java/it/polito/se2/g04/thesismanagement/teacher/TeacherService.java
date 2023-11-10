package it.polito.se2.g04.thesismanagement.teacher;

import it.polito.se2.g04.thesismanagement.group.Group;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface TeacherService {
    HashMap<String, Group> getAllTeacherGroup();
    List<Teacher> getAll();
    Optional<Teacher> getById(Long id);
}
