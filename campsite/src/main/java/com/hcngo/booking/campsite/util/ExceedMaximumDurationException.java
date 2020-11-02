package com.hcngo.booking.campsite.util;

public class ExceedMaximumDurationException extends RuntimeException {
    public ExceedMaximumDurationException(String message) {
        super(message);
    }
}
