package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.proposal;

public class UpdateAfterAcceptException  extends RuntimeException{
    public UpdateAfterAcceptException(String message){
        super(message);
    }
}
