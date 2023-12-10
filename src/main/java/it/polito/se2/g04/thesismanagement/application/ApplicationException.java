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
    @ExceptionHandler(ApplicationDeletedException.class)
    public ProblemDetail handleBadRequest(ApplicationDeletedException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }
    @ExceptionHandler(ProposalNotActiveException.class)
    public ProblemDetail handleBadRequest(ProposalNotActiveException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }
    @ExceptionHandler(DuplicateApplicationException.class)
    public ProblemDetail handleBadRequest(DuplicateApplicationException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }
}
class ApplicationBadRequestFormatException extends RuntimeException {
    public ApplicationBadRequestFormatException(String message) {
        super(message);
    }
}

class ApplicationDeletedException extends RuntimeException{
    public ApplicationDeletedException(String message){super(message);}
}
class ProposalNotActiveException extends RuntimeException{
    public ProposalNotActiveException(String message){super(message);}
}

class DuplicateApplicationException extends RuntimeException {
    public DuplicateApplicationException(String message) {super(message);}
}