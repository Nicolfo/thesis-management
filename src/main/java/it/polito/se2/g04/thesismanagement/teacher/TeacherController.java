package it.polito.se2.g04.thesismanagement.teacher;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService){
        this.teacherService=teacherService;
    }

    @GetMapping("API/teacher/getAll/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public List<TeacherDTO> getAll(){
        return teacherService.getAll();
    }

    @GetMapping("API/teacher/getById/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public Teacher getById(@PathVariable Long id){
        return teacherService.getById(id);
    }

    @GetMapping("API/teacher/getByEmail/{email}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public Teacher getByEmail(@PathVariable String email){
        return teacherService.getByEmail(email);
    }

    @GetMapping("API/teacher/getByEmail/")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public void getByEmailWithNoEmail(){
        //errorhandler placeholder
    }

    @GetMapping("API/teacher/getById/")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @PreAuthorize("isAuthenticated() && hasAuthority('TEACHER')")
    public void getByIdWithNoId(){
        //errorhandler placeholder
    }
}
