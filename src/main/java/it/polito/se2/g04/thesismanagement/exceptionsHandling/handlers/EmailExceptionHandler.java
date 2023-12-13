package it.polito.se2.g04.thesismanagement.exceptionsHandling.handlers;

import it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.email.EmailFailedSendException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EmailExceptionHandler {
    @ExceptionHandler(EmailFailedSendException.class)
    public ProblemDetail handleDepartmentNotFound(EmailFailedSendException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.GATEWAY_TIMEOUT,e.getMessage());
    }
}
