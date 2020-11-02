package com.hcngo.booking.campsite.controller;

import com.hcngo.booking.campsite.model.Reservation;
import com.hcngo.booking.campsite.service.ReservationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.print.attribute.standard.Media;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "/v1/booking", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
@RestControllerAdvice
public class BookingController {
    private ReservationService reservationService;
    
    @Autowired
    public BookingController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping(value = "/getAvailableDates")
    public Set<Date> getAvailableDates(@RequestParam String startDate, @RequestParam String endDate) throws ParseException {
        return reservationService.getAvailableDates(startDate, endDate);
    }

    @GetMapping(value = "")
    public Iterable<Reservation> get() {
        return reservationService.getAll();
    }

    @GetMapping(value = "/{id}")
    public Optional<Reservation> get(@PathVariable String id) {
        return reservationService.get(id);
    }

    @PostMapping(value = "")
    public Reservation create(@RequestBody Map<String, String> reservationDetails) throws ParseException {
        return reservationService.createReservation(reservationDetails.get("startDate"), reservationDetails.get("endDate"), reservationDetails.get("email"), reservationDetails.get("name"));
    }

    @DeleteMapping(value = "/{id}")
    public Optional<Reservation> delete(@PathVariable String id) {
        return reservationService.delete(id);
    }

    @PutMapping(value = "/{id}")
    public Optional<Reservation> update(@PathVariable String id, @RequestBody Map<String, String> reservationDetails) throws ParseException {
        return reservationService.updateReservation(id, reservationDetails);
    }
}
