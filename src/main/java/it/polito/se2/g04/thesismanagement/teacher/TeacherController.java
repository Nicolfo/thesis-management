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

    @GetMapping("API/teacher/getAllTeachersGroup")
    public HashMap<TeacherDTO,String> getAllTeachersGrouyp(){return  teacherService.getAllTeachersGroup();}

    @GetMapping("API/teacher/getAll/")
    public List<TeacherDTO> getAll(){
        return teacherService.getAll();
    }

    @GetMapping("API/teacher/getById/{id}")
    public Teacher getById(Long id){
        return teacherService.getById(id);
    }

    @GetMapping("API/teacher/getById/")
    public void getByIdWithNoId(){
        //errorhandler placeholder
    }
}
