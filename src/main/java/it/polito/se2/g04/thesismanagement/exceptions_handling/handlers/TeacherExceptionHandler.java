package it.polito.se2.g04.thesismanagement.exceptions_handling.handlers;

import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.teacher.InvalidTeacherException;
import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.teacher.TeacherNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TeacherExceptionHandler {
    @ExceptionHandler(TeacherNotFoundException.class)
    public ProblemDetail teacherNotFound(TeacherNotFoundException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,e.getMessage());
    }

    @ExceptionHandler(InvalidTeacherException.class)
    public ProblemDetail invalidTeacher(InvalidTeacherException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }
}
