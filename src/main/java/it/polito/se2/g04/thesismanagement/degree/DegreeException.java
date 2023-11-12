package it.polito.se2.g04.thesismanagement.degree;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DegreeException {
    @ExceptionHandler(DegreeNotFoundException.class)
    public ProblemDetail handleDegreeNotFound(DegreeNotFoundException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,e.getMessage());
    }
}
