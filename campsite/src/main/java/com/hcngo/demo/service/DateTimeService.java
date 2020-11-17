package com.hcngo.demo.service;

import com.hcngo.demo.util.Constants;

import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class DateTimeService {
    public Date getCurrentDate() {
        try {
            return Constants.sdformat.parse("2020-11-07");
        } catch (Exception ex) {
            return new Date();
        }
    }

    public long dateDiff(Date start, Date end) {
        long diffInMillies = end.getTime() - start.getTime();
        if (diffInMillies < 0) {
            long diff = TimeUnit.DAYS.convert(Math.abs(diffInMillies), TimeUnit.MILLISECONDS);
            return -diff;
        } else {
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            return diff;
        }
    }

    public Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
}
