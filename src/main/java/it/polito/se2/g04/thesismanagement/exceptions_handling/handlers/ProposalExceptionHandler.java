package it.polito.se2.g04.thesismanagement.exceptions_handling.handlers;

import it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProposalExceptionHandler {
    @ExceptionHandler(ProposalNotFoundException.class)
    public ProblemDetail proposalNotFound(ProposalNotFoundException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,e.getMessage());
    }

    @ExceptionHandler(ProposalOwnershipException.class)
    public ProblemDetail proposalOwnership(ProposalOwnershipException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,e.getMessage());
    }

    @ExceptionHandler(CreateUpdateProposalWithNoPathVariable.class)
    public ProblemDetail handleServiceNotFound(CreateUpdateProposalWithNoPathVariable e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DeleteWithNoId.class)
    public ProblemDetail handleServiceNotFound(DeleteWithNoId e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());

    }

    @ExceptionHandler(ProposalLevelInvalidException.class)
    public ProblemDetail handleServiceNotFound(ProposalLevelInvalidException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ArchiveWithNoId.class)
    public ProblemDetail handleServiceNotFound(ArchiveWithNoId e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UpdateAfterAcceptException.class)
    public ProblemDetail updateAfterAccepted(UpdateAfterAcceptException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(GetProposalWithNoId.class)
    public ProblemDetail getProposalWithNoId(GetProposalWithNoId e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
