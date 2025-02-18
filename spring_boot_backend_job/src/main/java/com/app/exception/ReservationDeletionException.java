package com.app.exception;

public class ReservationDeletionException extends RuntimeException {

    private static final long serialVersionUID = 1L;
        public ReservationDeletionException(String message) {
            super(message);
        }

}
