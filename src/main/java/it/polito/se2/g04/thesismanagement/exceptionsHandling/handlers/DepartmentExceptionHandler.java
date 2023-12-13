package it.polito.se2.g04.thesismanagement.exceptionsHandling.handlers;

import it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.degree.DegreeNotFoundException;
import it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.department.DepartmentNotFoundException;
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
