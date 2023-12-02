package it.polito.se2.g04.thesismanagement.proposal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProposalExceptions {
    @ExceptionHandler(JsonStringCantDeserialize.class)
    public ProblemDetail handleJsonStringCantDeserialize(JsonStringCantDeserialize e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }

    @ExceptionHandler(createUpdateProposalWithNoPathVariable.class)
    public ProblemDetail handleServiceNotFound(createUpdateProposalWithNoPathVariable e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }

    @ExceptionHandler(DeleteWithNoId.class)
    public ProblemDetail handleServiceNotFound(DeleteWithNoId e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }

    @ExceptionHandler(ProposalLevelInvalidException.class)
    public ProblemDetail handleServiceNotFound(ProposalLevelInvalidException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }

    @ExceptionHandler(ArchiveWithNoId.class)
    public ProblemDetail handleServiceNotFound(ArchiveWithNoId e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }
}

class JsonStringCantDeserialize extends RuntimeException{
    public JsonStringCantDeserialize(String msg) {super(msg);}
}

class createUpdateProposalWithNoPathVariable extends RuntimeException {
    public createUpdateProposalWithNoPathVariable(String message) {
        super(message);
    }
}

class DeleteWithNoId extends RuntimeException{
    public DeleteWithNoId(String message) {super(message);}
}
class ArchiveWithNoId extends RuntimeException{
    public ArchiveWithNoId(String message) {super(message);}
}