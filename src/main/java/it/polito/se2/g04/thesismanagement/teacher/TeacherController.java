package it.polito.se2.g04.thesismanagement.teacher;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/API/teacher/getAll")
    @ResponseStatus(HttpStatus.OK)
    public List<TeacherDTO> getAll() {
        return teacherService.getAllTeachers();
    }
}
