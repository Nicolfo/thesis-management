package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Handlers;

import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Application.ApplicationBadRequestFormatException;
import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Application.ApplicationDeletedException;
import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Application.DuplicateApplicationException;
import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Application.ProposalNotActiveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler(ApplicationBadRequestFormatException.class)
    public ProblemDetail handleBadRequestFormat(ApplicationBadRequestFormatException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }
    @ExceptionHandler(ApplicationDeletedException.class)
    public ProblemDetail handleBadRequest(ApplicationDeletedException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }
    @ExceptionHandler(ProposalNotActiveException.class)
    public ProblemDetail handleBadRequest(ProposalNotActiveException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }
    @ExceptionHandler(DuplicateApplicationException.class)
    public ProblemDetail handleDuplicateApplication(DuplicateApplicationException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,e.getMessage());
    }
}