package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Handlers;

import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Proposal.ProposalNotFoundException;
import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Student.StudentNotFoundException;
import it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Teacher.TeacherNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StudentExceptionHandler {
    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail studentNotFound(StudentNotFoundException e){
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,e.getMessage());
    }
}