package com.hcngo.booking.campsite.service;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DateTimeService {
    public Date getCurrentDate() {
        return new Date();
    }
}
