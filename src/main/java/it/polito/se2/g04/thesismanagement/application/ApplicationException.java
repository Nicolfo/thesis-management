package it.polito.se2.g04.thesismanagement.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationException {
    @ExceptionHandler(ApplicationBadRequestFormatException.class)
    public ProblemDetail handleBadRequestFormat(ApplicationBadRequestFormatException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }
}
class ApplicationBadRequestFormatException extends RuntimeException {
    public ApplicationBadRequestFormatException(String message) {
        super(message);
    }
}