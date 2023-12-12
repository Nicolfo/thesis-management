package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Handlers;

import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Degree.DegreeNotFoundException;
import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Department.DepartmentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DepartmentExceptionHandler {
    @ExceptionHandler(DepartmentNotFoundException.class)
    public ProblemDetail handleDepartmentNotFound(DegreeNotFoundException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,e.getMessage());
    }
}
