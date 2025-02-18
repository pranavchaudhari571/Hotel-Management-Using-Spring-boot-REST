package com.app.exception;

public class PaymentDeletionException extends RuntimeException{

    private static final long serialVersionUID = 1L;
        public PaymentDeletionException(String message) {
            super(message);
        }

}
