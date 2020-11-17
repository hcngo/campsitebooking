package com.hcngo.demo.util;

public class NotAvailableForBookingException extends RuntimeException {
    public NotAvailableForBookingException(String message) {
        super(message);
    }
}
