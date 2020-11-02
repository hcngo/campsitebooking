package com.hcngo.booking.campsite.util;

public class NotAvailableForBookingException extends RuntimeException {
    public NotAvailableForBookingException(String message) {
        super(message);
    }
}
