package com.hcngo.booking.campsite.service;

import com.hcngo.booking.campsite.model.Reservation;
import com.hcngo.booking.campsite.repository.ReservationRepository;
import com.hcngo.booking.campsite.util.AdvanceBookingException;
import com.hcngo.booking.campsite.util.Constants;
import com.hcngo.booking.campsite.util.ExceedMaximumDurationException;
import com.hcngo.booking.campsite.util.NotAvailableForBookingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository;
    private DateTimeService dateTimeService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, DateTimeService dateTimeService) {
        this.reservationRepository = reservationRepository;
        this.dateTimeService = dateTimeService;
    }

    public Iterable<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> get(String id) {
        return reservationRepository.findById(id);
    }

    public Set<Date> getAvailableDates(String startDate, String endDate) throws ParseException {
        return getAvailableDates(Constants.sdformat.parse(startDate), Constants.sdformat.parse(endDate), null);
    }

    public Reservation createReservation(String startDate, String endDate, String email, String name) throws ParseException {
        Date s = Constants.sdformat.parse(startDate);
        Date e = Constants.sdformat.parse(endDate);

        checkCreateReservationBusinessLogic(s, e);

        Reservation r = new Reservation(startDate, endDate, email, name);
        r = reservationRepository.save(r);
        return r;
    }

    public Optional<Reservation> delete(String id) {
        Optional<Reservation> record = get(id);
        record.ifPresent(reservation -> reservationRepository.delete(reservation));
        return record;
    }

    public Optional<Reservation> updateReservation(String id, Map<String, String> newDetails) throws ParseException {
        Optional<Reservation> rWrapper = reservationRepository.findById(id);
        if (!rWrapper.isPresent()) {
            return rWrapper;
        }
        Reservation r = rWrapper.get();
        Date newStartDate = Constants.sdformat.parse(newDetails.get("startDate"));
        Date newEndDate = Constants.sdformat.parse(newDetails.get("endDate"));

        if (getAvailableDates(newStartDate, newEndDate, r.getId()).size() != dateDiff(newStartDate, newEndDate) + 1) {
            throw new NotAvailableForBookingException("Cannot book for the duration due to unavailability!");
        }

        r.setEmail(newDetails.getOrDefault("email", r.getEmail()));
        r.setName(newDetails.getOrDefault("name", r.getName()));
        r.setStartDate(newStartDate);
        r.setEndDate(newEndDate);
        r = reservationRepository.save(r);

        return Optional.of(r);
    }

    private Set<Date> getAvailableDates(Date startDate, Date endDate, String ignoreThisReservationId) {
        Set<Date> availableDates = new HashSet<>();

        List<Reservation> l1 = reservationRepository.findReservationByStartDateBetween(startDate, endDate);
        List<Reservation> l2 = reservationRepository.findReservationByEndDateBetween(startDate, endDate);
        List<Reservation> l3 = reservationRepository.findReservationByStartDateLessThanEqualAndEndDateGreaterThanEqual(startDate, endDate);
        Set<Reservation> s = new HashSet<>();
        s.addAll(l1);
        s.addAll(l2);
        s.addAll(l3);
        // Filter out
        if (ignoreThisReservationId != null) {
            s = s.stream().filter(r -> !r.getId().equals(ignoreThisReservationId)).collect(Collectors.toSet());
        }
        for (int i = 0; i <= dateDiff(startDate, endDate); i++) {
            Date d = addDays(startDate, i);
            if (!s.stream().anyMatch(r -> d.compareTo(r.getStartDate()) >= 0 && d.compareTo(r.getEndDate()) <= 0)) {
                availableDates.add(d);
            }
        }
        return availableDates;
    }

    private void checkCreateReservationBusinessLogic(Date startDate, Date endDate) {
        // The campsite can be reserved for max 3 days.
        final int maxDays = 3;
        if (dateDiff(startDate, endDate) > maxDays) {
            throw new ExceedMaximumDurationException("The duration exceeds " + maxDays + " days!");
        }

        // The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.
        if (dateDiff(dateTimeService.getCurrentDate(), startDate) > 30 || dateDiff(dateTimeService.getCurrentDate(), startDate) < 1) {
            throw new AdvanceBookingException("The campsite must be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.");
        }

        if (getAvailableDates(startDate, endDate, null).size() != dateDiff(startDate, endDate) + 1) {
            throw new NotAvailableForBookingException("Cannot book for the duration due to unavailability!");
        }
    }

    private long dateDiff(Date start, Date end) {
        long diffInMillies = end.getTime() - start.getTime();
        if (diffInMillies < 0) {
            long diff = TimeUnit.DAYS.convert(Math.abs(diffInMillies), TimeUnit.MILLISECONDS);
            return -diff;
        } else {
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            return diff;
        }
    }

    private Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
}
