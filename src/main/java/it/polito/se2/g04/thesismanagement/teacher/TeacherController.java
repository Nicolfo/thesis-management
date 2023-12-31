package it.polito.se2.g04.thesismanagement.teacher;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("API/teacher/getAll")
    @ResponseStatus(HttpStatus.OK)
    public List<TeacherDTO> getAll(){
        return teacherService.getAll();
    }

    @GetMapping("API/teacher/getById/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public TeacherDTO getById(@PathVariable Long id){
        return teacherService.getById(id);
    }

    @GetMapping("API/teacher/getByEmail/{email}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public TeacherDTO getByEmail(@PathVariable String email){
        return teacherService.getByEmail(email);
    }

    @GetMapping("API/teacher/getByEmail")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public void getByEmailWithNoEmail(){
        //errorhandler placeholder
    }

    @GetMapping("API/teacher/getById")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @PreAuthorize("isAuthenticated() && hasRole('TEACHER')")
    public void getByIdWithNoId(){
        //errorhandler placeholder
    }
}
