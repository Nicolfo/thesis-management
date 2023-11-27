package it.polito.se2.g04.thesismanagement.teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;


    public TeacherServiceImpl(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll().stream().map(TeacherDTO::fromTeacher).toList();
    }

    @Override
    public List<TeacherDTO> getAll() {
        return teacherRepository.findAll().stream().map(TeacherDTO::fromTeacher).toList();

    }


    @Override
    public Teacher getById(Long id){
        return teacherRepository.findById(id).orElseThrow(()-> new RuntimeException("id not found"));
    }

    @Override
    public Teacher getByEmail(String email){
        return teacherRepository.findAll().stream().filter(it-> email.equals(it.getEmail())).findAny().orElseThrow(()-> new RuntimeException("email not found"));
    }
}
