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

    @ExceptionHandler(createProposalWithNoPathVariable.class)
    public ProblemDetail handleServiceNotFound(createProposalWithNoPathVariable e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,e.getMessage());
    }
}

class JsonStringCantDeserialize extends RuntimeException{
    public JsonStringCantDeserialize(String msg) {super(msg);}
}

class createProposalWithNoPathVariable extends RuntimeException {
    public createProposalWithNoPathVariable(String message) {
        super(message);
    }
}
