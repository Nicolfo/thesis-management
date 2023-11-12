package it.polito.se2.g04.thesismanagement.teacher;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;


    public TeacherServiceImpl(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.getAll().stream().map(t -> new TeacherDTO(t.getId(), t.getSurname(), t.getName(), t.getEmail(), t.getGroup(), t.getDepartment())).toList();
    }
}
