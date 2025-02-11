package com.app.exception;

public class ReservationConflictException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public ReservationConflictException(String message) {
        super(message);
    }
}
