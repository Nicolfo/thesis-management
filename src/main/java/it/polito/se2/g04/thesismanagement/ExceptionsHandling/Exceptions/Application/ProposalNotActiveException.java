package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Application;

public class ProposalNotActiveException extends RuntimeException{
    public ProposalNotActiveException(String message){super(message);}
}