package com.adam.rvc.util;

public class MalformedMacAddressException extends Exception {

    private final String message;

    public MalformedMacAddressException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
