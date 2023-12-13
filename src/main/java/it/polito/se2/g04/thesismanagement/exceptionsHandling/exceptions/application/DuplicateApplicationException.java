package it.polito.se2.g04.thesismanagement.exceptionsHandling.exceptions.application;

public class DuplicateApplicationException extends RuntimeException{
    public DuplicateApplicationException(String message){super(message);}
}