package it.polito.se2.g04.thesismanagement.proposalOnRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProposalOnRequestExeceptions {

    @ExceptionHandler(proposalRequestWithNoId.class)
    public ProblemDetail handleServiceNotFound(proposalRequestWithNoId e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

}

class proposalRequestWithNoId extends RuntimeException {
    public proposalRequestWithNoId(String msg) {
        super(msg);
    }
}