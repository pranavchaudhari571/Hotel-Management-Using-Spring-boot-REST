package com.app.exception;

public class ReservationNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;
        public ReservationNotFoundException(String message) {
            super(message);
        }
}
