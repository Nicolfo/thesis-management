package it.polito.se2.g04.thesismanagement.teacher;

import it.polito.se2.g04.thesismanagement.group.Group;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService){
        this.teacherService=teacherService;
    }

    @GetMapping("API/prof/getAllTeachersGroup/")
    public HashMap<String, Group> getAllTeachersGroup(){
        return teacherService.getAllTeacherGroup();
    }

    @GetMapping("API/prof/getAll/")
    public List<Teacher> getAll(){
        return teacherService.getAll();
    }

    @GetMapping("API/prof/getById/{id}")
    public Optional<Teacher> getById(Long id){
        return teacherService.getById(id);
    }

    @GetMapping("API/prof/getById/")
    public void getByIdWithNoId(){
        //errorhandler placeholder
    }
}
