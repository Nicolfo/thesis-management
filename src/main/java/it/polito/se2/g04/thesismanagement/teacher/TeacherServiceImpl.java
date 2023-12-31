package it.polito.se2.g04.thesismanagement.teacher;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;


    public TeacherServiceImpl(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Override
    public List<TeacherDTO> getAll() {
        return teacherRepository.findAll().stream().map(TeacherDTO::fromTeacher).toList();

    }


    @Override
    public TeacherDTO getById(Long id){
        return TeacherDTO.fromTeacher(teacherRepository.findById(id).orElseThrow(()-> new RuntimeException("id not found")));
    }

    @Override
    public TeacherDTO getByEmail(String email){
        return TeacherDTO.fromTeacher(teacherRepository.findByEmail(email));
    }
}
