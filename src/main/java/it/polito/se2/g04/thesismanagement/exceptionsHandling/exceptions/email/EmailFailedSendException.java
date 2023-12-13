package it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.email;

public class EmailFailedSendException extends RuntimeException{
    public EmailFailedSendException(String message){
        super(message);
    }
}
