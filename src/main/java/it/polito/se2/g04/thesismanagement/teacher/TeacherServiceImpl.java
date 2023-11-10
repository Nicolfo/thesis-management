package it.polito.se2.g04.thesismanagement.teacher;

import it.polito.se2.g04.thesismanagement.group.Group;
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
    public HashMap<String, Group> getAllTeacherGroup(){
        HashMap<String, Group> teachersGroup=new HashMap<>();
        List<Teacher> teachers=teacherRepository.findAll();
        for (Teacher t: teachers)
            teachersGroup.put(t.getSurname() + " " + t.getName(),t.getGroup());
        return teachersGroup;
    }

    @Override
    public List<Teacher> getAll(){
        return  teacherRepository.findAll();
    }
    @Override
    public Optional<Teacher> getById(Long id){
        return teacherRepository.findById(id);
    }
}
