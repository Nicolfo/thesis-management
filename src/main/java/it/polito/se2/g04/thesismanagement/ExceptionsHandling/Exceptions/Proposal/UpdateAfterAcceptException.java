package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Proposal;

public class UpdateAfterAcceptException  extends RuntimeException{
    public UpdateAfterAcceptException(String message){
        super(message);
    }
}
