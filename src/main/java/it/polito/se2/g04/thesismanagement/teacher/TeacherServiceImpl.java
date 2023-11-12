package it.polito.se2.g04.thesismanagement.teacher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private final TeacherRepository teacherRepository;
    
    public TeacherServiceImpl(TeacherRepository teacherRepository){
        this.teacherRepository=teacherRepository;
    }

    @Override
    public List<TeacherDTO> getAll(){
        return teacherRepository.findAll().stream().map(it-> new TeacherDTO(it.getId(),it.getName(), it.getSurname())).toList();
    }
    @Override
    public Teacher getById(Long id){
        return teacherRepository.findById(id).orElseThrow(()-> new RuntimeException());//placeholder for a real exception handler
    }
}
