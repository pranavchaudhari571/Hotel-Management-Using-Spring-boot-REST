package com.app.exception;

public class BookingDeletionException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public BookingDeletionException(String message) {
        super(message);
    }
}
