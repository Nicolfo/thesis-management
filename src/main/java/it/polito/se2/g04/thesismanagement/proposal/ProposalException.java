package it.polito.se2.g04.thesismanagement.proposal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProposalException {
    @ExceptionHandler(ProposalNotFoundException.class)
    public ProblemDetail proposalNotFound(ProposalNotFoundException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,e.getMessage());
    }

    @ExceptionHandler(ProposalOwnershipException.class)
    public ProblemDetail proposalOwnership(ProposalOwnershipException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,e.getMessage());
    }


}