package it.polito.se2.g04.thesismanagement.exceptions_handling.handlers;

import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.group.GroupNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GroupExceptionHandler {
    @ExceptionHandler(GroupNotFoundException.class)
    public ProblemDetail handleGroupNotFound(GroupNotFoundException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,e.getMessage());
    }
}