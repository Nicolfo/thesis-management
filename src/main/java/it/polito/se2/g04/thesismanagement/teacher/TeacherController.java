package it.polito.se2.g04.thesismanagement.teacher;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService){
        this.teacherService=teacherService;
    }

    @GetMapping("API/teacher/getAll/")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public List<TeacherDTO> getAll(){
        return teacherService.getAll();
    }

    @GetMapping("API/teacher/getById/{id}")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public Teacher getById(Long id){
        return teacherService.getById(id);
    }

    @GetMapping("API/teacher/getByEmail/{email}")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public Teacher getByEmail(String email){
        return teacherService.getByEmail(email);
    }

    @GetMapping("API/teacher/getByEmail/")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public void getByEmailWithNoEmail(){
        //errorhandler placeholder
    }

    @GetMapping("API/teacher/getById/")
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public void getByIdWithNoId(){
        //errorhandler placeholder
    }
}
