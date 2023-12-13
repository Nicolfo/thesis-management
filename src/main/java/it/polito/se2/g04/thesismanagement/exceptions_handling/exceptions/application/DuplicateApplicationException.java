package it.polito.se2.g04.thesismanagement.exceptions_handling.exceptions.application;

public class DuplicateApplicationException extends RuntimeException{
    public DuplicateApplicationException(String message){super(message);}
}