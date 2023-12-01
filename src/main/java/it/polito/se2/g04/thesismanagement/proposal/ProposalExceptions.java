package it.polito.se2.g04.thesismanagement.proposal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProposalExceptions {

    @ExceptionHandler(createUpdateProposalWithNoPathVariable.class)
    public ProblemDetail handleServiceNotFound(createUpdateProposalWithNoPathVariable e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(deleteWithNoId.class)
    public ProblemDetail handleServiceNotFound(deleteWithNoId e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ProposalLevelInvalidException.class)
    public ProblemDetail handleServiceNotFound(ProposalLevelInvalidException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(archiveWithNoId.class)
    public ProblemDetail handleServiceNotFound(archiveWithNoId e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(updateAfterAccepted.class)
    public ProblemDetail updateAfterAccepted(updateAfterAccepted e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}

class createUpdateProposalWithNoPathVariable extends RuntimeException {
    public createUpdateProposalWithNoPathVariable(String message) {
        super(message);
    }
}

class deleteWithNoId extends RuntimeException {
    public deleteWithNoId(String message) {
        super(message);
    }
}

class archiveWithNoId extends RuntimeException {
    public archiveWithNoId(String message) {
        super(message);
    }
}

class updateAfterAccepted extends RuntimeException {
    public updateAfterAccepted(String message) {
        super(message);
    }
}