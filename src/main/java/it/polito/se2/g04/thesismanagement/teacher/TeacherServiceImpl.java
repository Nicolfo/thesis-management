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
    public HashMap<TeacherDTO, String> getAllTeachersGroup(){
        return teacherRepository.findAll().stream().map(it-> new HashMap<TeacherDTO,String>((new TeacherDTO(it.getId(),it.getName(), it.getSurname()),it.getGroup().getCodGroup()));
    }
    @Override
    public List<TeacherDTO> getAll(){
        return teacherRepository.findAll().stream().map(it-> new TeacherDTO(it.getId(),it.getName(), it.getSurname())).toList();
    }
    @Override
    public Teacher getById(Long id){
        Optional<Teacher> teacher= teacherRepository.findById(id);
        if(teacher.isPresent())
            return teacher.get();
        else{
            // trhow exception
            return null;
        }
    }
}
