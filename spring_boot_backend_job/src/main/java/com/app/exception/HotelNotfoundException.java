package com.app.exception;

public class HotelNotfoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public HotelNotfoundException(String message) {
        super(message);
    }
}
