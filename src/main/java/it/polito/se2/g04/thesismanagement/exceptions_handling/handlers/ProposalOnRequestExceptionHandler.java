package it.polito.se2.g04.thesismanagement.exceptions_handling.handlers;

import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal_on_request.ProposalRequestWithNoId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProposalOnRequestExceptionHandler {

    @ExceptionHandler(ProposalRequestWithNoId.class)
    public ProblemDetail handleServiceNotFound(ProposalRequestWithNoId e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

}
