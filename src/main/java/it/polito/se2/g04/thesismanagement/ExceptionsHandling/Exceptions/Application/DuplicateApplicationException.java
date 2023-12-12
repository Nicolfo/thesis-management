package it.polito.se2.g04.thesismanagement.ExceptionsHandling.Exceptions.Application;

public class DuplicateApplicationException extends RuntimeException{
    public DuplicateApplicationException(String message){super(message);}
}